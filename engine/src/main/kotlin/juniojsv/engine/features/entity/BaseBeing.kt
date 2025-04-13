package juniojsv.engine.features.entity

import com.bulletphysics.collision.dispatch.CollisionObject
import com.bulletphysics.dynamics.RigidBody
import com.bulletphysics.dynamics.RigidBodyConstructionInfo
import com.bulletphysics.linearmath.DefaultMotionState
import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.texture.Texture
import juniojsv.engine.features.utils.IBoundaryShape
import juniojsv.engine.features.utils.Transform as EntityTransform

class BaseBeing(
    val transform: EntityTransform = EntityTransform(),
    val texture: Texture? = null,
    val textureScale: Float = 1f,
    val mass: Float = 10f,
    val restitution: Float = .3f,
    val friction: Float = .5f,
    val linearDamping: Float = 0f,
    val angularDamping: Float = .5f,
    val angularFactor: Float = 1f
) {
    var collisionObject: CollisionObject? = null
        private set

    fun applyCollisionObjectTransform() = collisionObject?.also { transform.set(it) }

    fun setAsRigidBody(context: IWindowContext, boundary: IBoundaryShape) {
        dispose(context)
        val scale = transform.scale
        val shape = boundary.getCollisionShape(scale)
        val inertia = javax.vecmath.Vector3f()
        if (mass > 0f) shape.calculateLocalInertia(mass, inertia)
        val transform = transform.toBulletTransform()
        val config = RigidBodyConstructionInfo(mass, DefaultMotionState(transform), shape, inertia)
        config.additionalDamping = true
        config.restitution = restitution
        config.friction = friction
        config.linearDamping = linearDamping
        config.angularDamping = angularDamping

        val body = RigidBody(config).also { it.userPointer = this }
        body.angularFactor = angularFactor

        context.physics.addCollisionObject(body)
        collisionObject = body
    }

    fun dispose(context: IWindowContext) {
        collisionObject.let {
            if (it == null) return
            collisionObject = null
            context.physics.removeCollisionObject(it)
        }
    }
}
