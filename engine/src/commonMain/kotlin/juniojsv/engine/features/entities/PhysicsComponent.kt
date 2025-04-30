package juniojsv.engine.features.entities

import com.bulletphysics.collision.dispatch.CollisionObject
import com.bulletphysics.collision.shapes.CollisionShape
import com.bulletphysics.dynamics.RigidBody
import com.bulletphysics.dynamics.RigidBodyConstructionInfo
import com.bulletphysics.linearmath.DefaultMotionState
import com.bulletphysics.linearmath.TransformUtil
import juniojsv.engine.extensions.BulletTransform
import juniojsv.engine.extensions.VecmathVector3f
import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.context.PhysicsContext
import juniojsv.engine.features.utils.IBoundaryShape
import juniojsv.engine.features.utils.Transform
import org.slf4j.LoggerFactory

interface PhysicsComponentListener {
    fun didTransformChanged()
}

class PhysicsComponent(
    val transform: Transform,
    val config: PhysicsConfig
) {
    companion object {
        private const val ADDITIONAL_DAMPING_ENABLED = false
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    private val listeners = mutableSetOf<PhysicsComponentListener>()

    private val collisionObjectTransform = BulletTransform()
    private val collisionObjectLinearVelocity = VecmathVector3f()
    private val collisionObjectAngularVelocity = VecmathVector3f()
    private val collisionObjectPredictedTransform = BulletTransform()

    private val previousTransform = Transform(transform)
    private val currentTransform = Transform(transform)
    private val predictedTransform = Transform(transform)
    private val interpolatedTransform = Transform(transform)

    var collisionObject: CollisionObject? = null
        private set

    fun onSimulationUpdate() {
        collisionObject?.also { collisionObject ->
            previousTransform.set(currentTransform)
            transform.previous.set(previousTransform)

            collisionObject.getWorldTransform(collisionObjectTransform)
            currentTransform.set(collisionObjectTransform)
            transform.set(currentTransform)

            (collisionObject as? RigidBody)?.also {
                it.getLinearVelocity(collisionObjectLinearVelocity)
                it.getAngularVelocity(collisionObjectAngularVelocity)
                TransformUtil.integrateTransform(
                    collisionObjectTransform,
                    collisionObjectLinearVelocity,
                    collisionObjectAngularVelocity,
                    PhysicsContext.FIXED_TIME_STEP,
                    collisionObjectPredictedTransform
                )
            } ?: collisionObjectPredictedTransform.set(collisionObjectTransform)

            predictedTransform.set(collisionObjectPredictedTransform)
            try {
                listeners.forEach { it.didTransformChanged() }
            } catch (e: Exception) {
                logger.error(e.message, e.cause)
            }
        }
    }

    fun onSimulationInterpolate(t: Float) {
        interpolatedTransform
            .set(currentTransform)
            .interpolate(predictedTransform, t)

        transform.set(interpolatedTransform)

        interpolatedTransform
            .set(previousTransform)
            .interpolate(currentTransform, t)

        transform.previous.set(interpolatedTransform)
        try {
            listeners.forEach { it.didTransformChanged() }
        } catch (e: Exception) {
            logger.error(e.message, e.cause)
        }
    }

    private fun calculateInertia(shape: CollisionShape): VecmathVector3f {
        val mass = config.mass
        val inertia = VecmathVector3f()
        if (mass > 0f) shape.calculateLocalInertia(mass, inertia)

        return inertia
    }

    fun createRigidBody(
        context: IWindowContext,
        boundary: IBoundaryShape
    ) {
        disposeCollisionObject(context)
        val (mass, restitution, friction, linearDamping, angularDamping, angularFactor) = config
        val scale = transform.scale
        val shape = boundary.createCollisionShape(scale)
        val inertia = calculateInertia(shape)
        val transform = transform.toBulletTransform()
        val config = RigidBodyConstructionInfo(
            mass,
            DefaultMotionState(transform),
            shape,
            inertia
        )
        config.additionalDamping = ADDITIONAL_DAMPING_ENABLED
        config.restitution = restitution
        config.friction = friction
        config.linearDamping = linearDamping
        config.angularDamping = angularDamping

        val body = RigidBody(config).also { it.userPointer = this }
        body.angularFactor = angularFactor

        context.physics.addCollisionObject(body)
        collisionObject = body
    }

    fun addListener(listener: PhysicsComponentListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: PhysicsComponentListener) {
        listeners.remove(listener)
    }

    private fun disposeCollisionObject(context: IWindowContext) {
        collisionObject?.let {
            collisionObject = null
            context.physics.removeCollisionObject(it)
        }
    }

    fun dispose(context: IWindowContext) {
        listeners.clear()
        disposeCollisionObject(context)
    }
}