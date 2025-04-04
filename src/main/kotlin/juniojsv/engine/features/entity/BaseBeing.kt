package juniojsv.engine.features.entity

import com.bulletphysics.dynamics.RigidBody
import com.bulletphysics.dynamics.RigidBodyConstructionInfo
import com.bulletphysics.linearmath.DefaultMotionState
import com.bulletphysics.linearmath.Transform
import juniojsv.engine.extensions.toJoml
import juniojsv.engine.extensions.toVecmath
import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.texture.Texture
import juniojsv.engine.features.utils.IBoundaryShape
import org.joml.Quaternionf
import org.joml.Vector3f
import javax.vecmath.Quat4f

class BaseBeing(
    val transform: juniojsv.engine.features.entity.Transform = juniojsv.engine.features.entity.Transform(),
    val texture: Texture? = null,
    val textureScale: Float = 1f,
    val mass: Float = 10f
) {
    private var body: RigidBody? = null

    fun updateTransform() {
        if (body == null) return
        val worldTransform = Transform()
        body?.motionState?.getWorldTransform(worldTransform)

        val position = worldTransform.origin
        transform.position.set(position.x, position.y, position.z)

        val rotation = Quat4f()
        worldTransform.getRotation(rotation)
        rotation.toJoml().also {
            val euler = Vector3f()
            it.getEulerAnglesXYZ(euler)

            val eulerDegrees = Vector3f(
                Math.toDegrees(euler.x.toDouble()).toFloat(),
                Math.toDegrees(euler.y.toDouble()).toFloat(),
                Math.toDegrees(euler.z.toDouble()).toFloat()
            )
            transform.rotation.set(eulerDegrees)
        }
    }

    fun createRigidBody(context: IWindowContext, boundary: IBoundaryShape) {
        val position = transform.position
        val scale = transform.scale
        val rotation = transform.rotation
        val shape = boundary.getCollisionShape(scale)
        val inertia = javax.vecmath.Vector3f()
        val massScale = scale.x * scale.y * scale.z
        if (mass > 0f) shape.calculateLocalInertia(mass * massScale, inertia)
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
        val body = RigidBody(
            RigidBodyConstructionInfo(
                mass, DefaultMotionState(transform), shape, inertia
            )
        ).apply {
            userPointer = this@BaseBeing
            friction = .2f
            restitution = .2f
        }

        context.physics.world.addRigidBody(body)
        this.body = body
    }

    fun disposeRigidBody(context: IWindowContext) {
        val body = this.body
        this.body = null
        context.physics.world.removeRigidBody(body)
    }
}