package juniojsv.engine.features.utils.factories

import juniojsv.engine.features.texture.FileCubeMapTexture
import juniojsv.engine.features.texture.FileTexture
import juniojsv.engine.features.texture.Texture

enum class Textures(val path: Any) {
    METAL_01("textures/metal/Metal_01-256x256.png"),
    METAL_02("textures/metal/Metal_02-256x256.png"),
    METAL_03("textures/metal/Metal_03-256x256.png"),
    METAL_04("textures/metal/Metal_04-256x256.png"),
    METAL_05("textures/metal/Metal_05-256x256.png"),
    METAL_06("textures/metal/Metal_06-256x256.png"),
    METAL_07("textures/metal/Metal_07-256x256.png"),
    METAL_08("textures/metal/Metal_08-256x256.png"),
    METAL_09("textures/metal/Metal_09-256x256.png"),
    METAL_10("textures/metal/Metal_10-256x256.png"),
    METAL_11("textures/metal/Metal_11-256x256.png"),
    METAL_12("textures/metal/Metal_12-256x256.png"),
    METAL_13("textures/metal/Metal_13-256x256.png"),
    METAL_14("textures/metal/Metal_14-256x256.png"),
    METAL_15("textures/metal/Metal_15-256x256.png"),
    METAL_16("textures/metal/Metal_16-256x256.png"),
    METAL_17("textures/metal/Metal_17-256x256.png"),
    METAL_18("textures/metal/Metal_18-256x256.png"),
    METAL_19("textures/metal/Metal_19-256x256.png"),
    METAL_20("textures/metal/Metal_20-256x256.png"),
    TEST("textures/uv.jpeg"),
    SKYBOX(
        arrayOf(
            "textures/sunset_bay_skybox/SunsetBay_E.png",
            "textures/sunset_bay_skybox/SunsetBay_W.png",
            "textures/sunset_bay_skybox/SunsetBay_U.png",
            "textures/sunset_bay_skybox/SunsetBay_D.png",
            "textures/sunset_bay_skybox/SunsetBay_N.png",
            "textures/sunset_bay_skybox/SunsetBay_S.png"
        )
    )
}

object TextureFactory {

    private val textures: MutableMap<Textures, Texture> = mutableMapOf()

    fun create(texture: Textures): Texture {
        return textures.getOrPut(texture) {
            when (val path = texture.path) {
                is String -> FileTexture(path)
                is Array<*> -> FileCubeMapTexture(path.filterIsInstance<String>().toTypedArray())
                else -> throw IllegalArgumentException("Invalid texture path for $texture")
            }
        }
    }

    fun createTexture(texture: Textures): FileTexture = when (texture.path) {
        is String -> create(texture) as FileTexture
        else -> throw IllegalArgumentException("Invalid texture type for $texture, expected String")
    }

    fun createCubeMapTexture(texture: Textures): FileCubeMapTexture = when (texture.path) {
        is Array<*> -> create(texture) as FileCubeMapTexture
        else -> throw IllegalArgumentException("Invalid texture type for $texture, expected Array<String>")
    }
}
