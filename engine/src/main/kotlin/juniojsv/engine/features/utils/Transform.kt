package juniojsv.engine.features.utils

import com.bulletphysics.collision.dispatch.CollisionObject
import juniojsv.engine.extensions.BulletTransform
import juniojsv.engine.extensions.toJoml
import juniojsv.engine.extensions.toVecmath
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f
import javax.vecmath.Quat4f

data class Transform(
    val position: Vector3f = Vector3f(0f),
    val rotation: Vector3f = Vector3f(0f),
    val scale: Vector3f = Vector3f(Scale.METER.length(1f))
) {
    val previous by lazy { Transform().also { it.set(this) } }

    fun setAsPrevious() {
        previous.set(this)
    }

    fun set(source: Transform) {
        position.set(source.position)
        rotation.set(source.rotation)
        scale.set(source.scale)
    }

    fun set(source: CollisionObject) {
        val worldTransform = BulletTransform()
        source.getWorldTransform(worldTransform)

        set(worldTransform)
    }

    fun set(source: BulletTransform) {
        position.set(source.origin.toJoml())

        val rotation = Quat4f()
        source.getRotation(rotation)
        rotation.toJoml().also {
            val euler = Vector3f()
            it.getEulerAnglesXYZ(euler)

            val eulerDegrees = Vector3f(
                Math.toDegrees(euler.x.toDouble()).toFloat(),
                Math.toDegrees(euler.y.toDouble()).toFloat(),
                Math.toDegrees(euler.z.toDouble()).toFloat()
            )
            this.rotation.set(eulerDegrees)
        }
    }

    fun toBulletTransform(): BulletTransform {
        val transform = BulletTransform().apply {
            setIdentity()
            origin.set(position.x, position.y, position.z)
            val quaternion = Quaternionf().rotateXYZ(
                Math.toRadians(rotation.x.toDouble()).toFloat(),
                Math.toRadians(rotation.y.toDouble()).toFloat(),
                Math.toRadians(rotation.z.toDouble()).toFloat()
            )
            setRotation(quaternion.toVecmath())
        }
        return transform
    }

    fun transformation(): Matrix4f = Matrix4f()
        .apply {
            translate(position)
            rotateX(Math.toRadians(rotation.x.toDouble()).toFloat())
            rotateY(Math.toRadians(rotation.y.toDouble()).toFloat())
            rotateZ(Math.toRadians(rotation.z.toDouble()).toFloat())
            scale(scale)
        }

    fun isRotated(): Boolean {
        return rotation.x != 0f || rotation.y != 0f || rotation.z != 0f
    }
}