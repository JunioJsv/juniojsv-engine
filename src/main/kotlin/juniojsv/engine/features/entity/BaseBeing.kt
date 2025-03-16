package juniojsv.engine.features.entity

import juniojsv.engine.features.texture.Texture
import juniojsv.engine.features.utils.Scale
import org.joml.Matrix4f
import org.joml.Vector3f

data class BaseBeing(
    val texture: Texture? = null,
    val position: Vector3f = Vector3f(0f),
    val rotation: Vector3f = Vector3f(0f),
    val scale: Vector3f = Vector3f(Scale.METER.length(1f)),
    val textureScale: Float = 1f
) {
    fun transformation(): Matrix4f = Matrix4f()
        .apply {
            translate(position)
            rotate(Math.toRadians(rotation.x.toDouble()).toFloat(), 1f, 0f, 0f)
            rotate(Math.toRadians(rotation.y.toDouble()).toFloat(), 0f, 1f, 0f)
            rotate(Math.toRadians(rotation.z.toDouble()).toFloat(), 0f, 0f, 1f)
            scale(scale)
        }

    fun transformedPosition(): Vector3f {
        val position = Vector3f()
        transformation().transformPosition(Vector3f(0f), position)
        return position
    }
}