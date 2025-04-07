package juniojsv.engine.features.entity

import com.bulletphysics.dynamics.RigidBody
import com.bulletphysics.dynamics.RigidBodyConstructionInfo
import com.bulletphysics.linearmath.DefaultMotionState
import com.bulletphysics.linearmath.Transform
import juniojsv.engine.extensions.toVecmath
import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.texture.Texture
import juniojsv.engine.features.utils.IBoundaryShape
import org.joml.Quaternionf
import juniojsv.engine.features.entity.Transform as EntityTransform

class BaseBeing(
    val transform: EntityTransform = EntityTransform(),
    val texture: Texture? = null,
    val textureScale: Float = 1f,
    val mass: Float = 10f
) {
    private var body: RigidBody? = null

    fun applyRigidBodyTransform() = body?.also { transform.set(it) }

    fun createRigidBody(context: IWindowContext, boundary: IBoundaryShape) {
        val position = transform.position
        val scale = transform.scale
        val rotation = transform.rotation
        val shape = boundary.getCollisionShape(scale)
        val inertia = javax.vecmath.Vector3f()
        if (mass > 0f) shape.calculateLocalInertia(mass, inertia)
        val transform = Transform().apply {
            setIdentity()
            origin.set(position.x, position.y, position.z)
            val quaternion = Quaternionf().rotateXYZ(
                Math.toRadians(rotation.x.toDouble()).toFloat(),
                Math.toRadians(rotation.y.toDouble()).toFloat(),
                Math.toRadians(rotation.z.toDouble()).toFloat()
            )
            setRotation(quaternion.toVecmath())
        }
        val config = RigidBodyConstructionInfo(mass, DefaultMotionState(transform), shape, inertia)
        config.additionalDamping = true
        val body = RigidBody(config)
        body.userPointer = this

        context.physics.world.addRigidBody(body)
        this.body = body
    }

    fun disposeRigidBody(context: IWindowContext) {
        val body = this.body
        this.body = null
        context.physics.world.removeRigidBody(body)
    }
}