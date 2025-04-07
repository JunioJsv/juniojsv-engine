package juniojsv.engine.features.entity

import com.bulletphysics.collision.dispatch.CollisionObject
import juniojsv.engine.extensions.toJoml
import juniojsv.engine.features.utils.Scale
import org.joml.Matrix4f
import org.joml.Vector3f
import javax.vecmath.Quat4f
import com.bulletphysics.linearmath.Transform as BulletTransform

data class Transform(
    val position: Vector3f = Vector3f(0f),
    val rotation: Vector3f = Vector3f(0f),
    val scale: Vector3f = Vector3f(Scale.METER.length(1f))
) {

    fun interpolate(to: Transform, factor: Float) {
        position.lerp(to.position, factor)
        rotation.lerp(to.rotation, factor)
        scale.lerp(to.scale, factor)
    }

    fun set(source: Transform) {
        position.set(source.position)
        rotation.set(source.rotation)
        scale.set(source.scale)
    }

    fun set(source: CollisionObject) {
        val worldTransform = BulletTransform()
        source.getWorldTransform(worldTransform)

        position.set(worldTransform.origin.toJoml())

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
            this.rotation.set(eulerDegrees)
        }
    }

    fun transformation(): Matrix4f = Matrix4f()
        .apply {
            translate(position)
            rotateX(Math.toRadians(rotation.x.toDouble()).toFloat())
            rotateY(Math.toRadians(rotation.y.toDouble()).toFloat())
            rotateZ(Math.toRadians(rotation.z.toDouble()).toFloat())
            scale(scale)
        }

    fun hasRotation(): Boolean {
        return rotation.x != 0f || rotation.y != 0f || rotation.z != 0f
    }
}