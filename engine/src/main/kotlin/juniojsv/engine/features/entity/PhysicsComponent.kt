package juniojsv.engine.features.entity

import com.bulletphysics.collision.dispatch.CollisionObject
import com.bulletphysics.collision.shapes.CollisionShape
import com.bulletphysics.dynamics.RigidBody
import com.bulletphysics.dynamics.RigidBodyConstructionInfo
import com.bulletphysics.linearmath.DefaultMotionState
import juniojsv.engine.extensions.VecmathVector3f
import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.utils.IBoundaryShape
import juniojsv.engine.features.utils.Transform

interface PhysicsComponentListener {
    fun didTransformChanged()
}

class PhysicsComponent(
    val transform: Transform,
    val config: PhysicsConfig
) {
    companion object {
        private const val ADDITIONAL_DAMPING_ENABLED = false
    }

    private val listeners = mutableSetOf<PhysicsComponentListener>()

    var collisionObject: CollisionObject? = null
        private set

    fun syncWithCollisionObject() {
        collisionObject?.let { collisionObject ->
            transform.set(collisionObject)
            listeners.forEach { it.didTransformChanged() }
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