package juniojsv.engine.features.utils.factories

import juniojsv.engine.features.texture.ColorTexture
import org.joml.Vector3f

object ColorTextureFactory {
    fun create(color: Vector3f) = ColorTexture(1, 1, color)
}