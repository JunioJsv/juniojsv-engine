package juniojsv.engine.features.entity

import juniojsv.engine.features.texture.TwoDimensionTexture
import juniojsv.engine.features.utils.BoundaryShape
import juniojsv.engine.features.utils.Scale
import org.joml.Matrix4f
import org.joml.Vector3f

data class BaseBeing(
    val texture: TwoDimensionTexture?,
    val position: Vector3f = Vector3f(0f),
    val rotation: Vector3f = Vector3f(0f),
    val scale: Float = Scale.METER.length(1f),
    val boundary: BoundaryShape? = null
) {
    fun transformation(): Matrix4f = Matrix4f()
        .apply {
            translate(position)
            rotate(Math.toRadians(rotation.x.toDouble()).toFloat(), 1f, 0f, 0f)
            rotate(Math.toRadians(rotation.y.toDouble()).toFloat(), 0f, 1f, 0f)
            rotate(Math.toRadians(rotation.z.toDouble()).toFloat(), 0f, 0f, 1f)
            scale(scale)
        }
}