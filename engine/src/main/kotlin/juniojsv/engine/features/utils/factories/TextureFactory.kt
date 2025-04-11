package juniojsv.engine.features.utils.factories

import juniojsv.engine.features.texture.ColorTexture
import juniojsv.engine.features.texture.FileCubeMapTexture
import juniojsv.engine.features.texture.FileTexture
import juniojsv.engine.features.texture.Texture
import org.joml.Vector3f

object TextureFactory {
    private val paths = mutableMapOf<String, Any>()

    private val textures: MutableMap<String, Texture> = mutableMapOf()

    fun create(texture: String): Texture {
        return textures.getOrPut(texture) {
            when (val path = paths[texture]) {
                is String -> FileTexture(path)
                is Array<*> -> FileCubeMapTexture(path.filterIsInstance<String>().toTypedArray())
                else -> throw IllegalArgumentException("Invalid texture path for $texture")
            }
        }
    }

    fun createTexture(texture: String): FileTexture = when (paths[texture]) {
        is String -> create(texture) as FileTexture
        else -> throw IllegalArgumentException("Invalid texture type for '$texture', expected String")
    }

    fun createCubeMapTexture(texture: String): FileCubeMapTexture = when (paths[texture]) {
        is Array<*> -> create(texture) as FileCubeMapTexture
        else -> throw IllegalArgumentException("Invalid texture type for '$texture', expected Array<String>")
    }

    fun createColorTexture(color: Vector3f, width: Int = 1, height: Int = 1) = ColorTexture(width, height, color)

    fun registry(texture: String, path: Any) {
        if (path !is String && (path is Array<*> && !path.isArrayOf<String>())) {
            throw IllegalArgumentException("Path for '$texture' must be a String or an Array<String>")
        }
        paths[texture] = path
        textures.remove(texture)
    }

    fun registry(textures: Map<String, Any>) {
        for (texture in textures) {
            registry(texture.key, texture.value)
        }
    }
}
