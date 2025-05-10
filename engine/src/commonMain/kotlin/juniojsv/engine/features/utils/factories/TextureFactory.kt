package juniojsv.engine.features.utils.factories

import juniojsv.engine.features.textures.ColorTexture
import juniojsv.engine.features.textures.FileCubeMapTexture
import juniojsv.engine.features.textures.FileTexture
import juniojsv.engine.features.textures.Texture
import org.joml.Vector3f

fun Array<*>.isCubeMap(): Boolean {
    return this.isArrayOf<String>() && this.size == 6
}

object TextureFactory {
    private val paths = mutableMapOf<String, Any>()

    private val textures: MutableMap<String, Texture> = mutableMapOf()

    fun create(texture: String): Texture {
        return textures.getOrPut(texture) {
            val path = paths[texture]

            if (path is String) {
                return FileTexture(path)
            } else if (path is Array<*> && path.isCubeMap()) {
                return FileCubeMapTexture((path as Array<String>))
            }

            throw IllegalArgumentException("Invalid texture path for $texture")
        }
    }

    fun getCubeMaps(): List<FileCubeMapTexture> {
        return paths.filter {
            val path = it.value
            path is Array<*> && path.isCubeMap()
        }.map { createCubeMapTexture(it.key) }
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
