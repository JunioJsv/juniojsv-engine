package juniojsv.engine.features.utils.factories

import juniojsv.engine.features.texture.FileCubeMapTexture
import juniojsv.engine.features.texture.FileTexture
import juniojsv.engine.features.texture.Texture

enum class Textures {
    METAL_01,
    METAL_02,
    METAL_03,
    METAL_04,
    METAL_05,
    METAL_06,
    METAL_07,
    METAL_08,
    METAL_09,
    METAL_10,
    METAL_11,
    METAL_12,
    METAL_13,
    METAL_14,
    METAL_15,
    METAL_16,
    METAL_17,
    METAL_18,
    METAL_19,
    METAL_20,
    TEST,
    SKYBOX
}

object TextureFactory {
    private val texturePaths: Map<Textures, Any> = mapOf(
        Textures.METAL_01 to "textures/metal/Metal_01-256x256.png",
        Textures.METAL_02 to "textures/metal/Metal_02-256x256.png",
        Textures.METAL_03 to "textures/metal/Metal_03-256x256.png",
        Textures.METAL_04 to "textures/metal/Metal_04-256x256.png",
        Textures.METAL_05 to "textures/metal/Metal_05-256x256.png",
        Textures.METAL_06 to "textures/metal/Metal_06-256x256.png",
        Textures.METAL_07 to "textures/metal/Metal_07-256x256.png",
        Textures.METAL_08 to "textures/metal/Metal_08-256x256.png",
        Textures.METAL_09 to "textures/metal/Metal_09-256x256.png",
        Textures.METAL_10 to "textures/metal/Metal_10-256x256.png",
        Textures.METAL_11 to "textures/metal/Metal_11-256x256.png",
        Textures.METAL_12 to "textures/metal/Metal_12-256x256.png",
        Textures.METAL_13 to "textures/metal/Metal_13-256x256.png",
        Textures.METAL_14 to "textures/metal/Metal_14-256x256.png",
        Textures.METAL_15 to "textures/metal/Metal_15-256x256.png",
        Textures.METAL_16 to "textures/metal/Metal_16-256x256.png",
        Textures.METAL_17 to "textures/metal/Metal_17-256x256.png",
        Textures.METAL_18 to "textures/metal/Metal_18-256x256.png",
        Textures.METAL_19 to "textures/metal/Metal_19-256x256.png",
        Textures.METAL_20 to "textures/metal/Metal_20-256x256.png",
        Textures.TEST to "textures/uv.jpeg",
        Textures.SKYBOX to arrayOf(
            "textures/sunset_bay_skybox/SunsetBay_E.png",
            "textures/sunset_bay_skybox/SunsetBay_W.png",
            "textures/sunset_bay_skybox/SunsetBay_U.png",
            "textures/sunset_bay_skybox/SunsetBay_D.png",
            "textures/sunset_bay_skybox/SunsetBay_N.png",
            "textures/sunset_bay_skybox/SunsetBay_S.png"
        )
    )

    private val textures: MutableMap<Textures, Texture> = mutableMapOf()

    fun create(texture: Textures): Texture {
        return textures.getOrPut(texture) {
            when (val path = texturePaths[texture]) {
                is String -> FileTexture(path)
                is Array<*> -> FileCubeMapTexture(path.filterIsInstance<String>().toTypedArray())
                else -> throw IllegalArgumentException("Invalid texture path for $texture")
            }
        }
    }

    fun createTexture(texture: Textures): FileTexture {
        return create(texture) as FileTexture
    }

    fun createCubeMapTexture(texture: Textures): FileCubeMapTexture {
        return create(texture) as FileCubeMapTexture
    }
}