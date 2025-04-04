package juniojsv.engine.features.entity

import juniojsv.engine.features.utils.Scale
import org.joml.Matrix4f
import org.joml.Vector3f

data class Transform(
    val position: Vector3f = Vector3f(0f),
    val rotation: Vector3f = Vector3f(0f),
    val scale: Vector3f = Vector3f(Scale.METER.length(1f))
) {
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