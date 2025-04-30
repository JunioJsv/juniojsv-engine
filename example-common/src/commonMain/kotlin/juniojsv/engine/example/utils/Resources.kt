package juniojsv.engine.example.utils

import juniojsv.engine.features.utils.Files
import juniojsv.engine.features.utils.ResourcesConfig
import juniojsv.engine.features.utils.ShadersFiles

val ResourcesCommon = ResourcesConfig(
    shaders = mapOf(
        "TEST" to ShadersFiles(
            vertex = "shaders/default/default.vert",
            fragment = "shaders/test.frag"
        )
    ),
    textures = mapOf(
        "METAL_01" to Files.Single("textures/metal/Metal_01-256x256.png"),
        "METAL_02" to Files.Single("textures/metal/Metal_02-256x256.png"),
        "METAL_03" to Files.Single("textures/metal/Metal_03-256x256.png"),
        "METAL_04" to Files.Single("textures/metal/Metal_04-256x256.png"),
        "METAL_05" to Files.Single("textures/metal/Metal_05-256x256.png"),
        "METAL_06" to Files.Single("textures/metal/Metal_06-256x256.png"),
        "METAL_07" to Files.Single("textures/metal/Metal_07-256x256.png"),
        "METAL_08" to Files.Single("textures/metal/Metal_08-256x256.png"),
        "METAL_09" to Files.Single("textures/metal/Metal_09-256x256.png"),
        "METAL_10" to Files.Single("textures/metal/Metal_10-256x256.png"),
        "METAL_11" to Files.Single("textures/metal/Metal_11-256x256.png"),
        "METAL_12" to Files.Single("textures/metal/Metal_12-256x256.png"),
        "METAL_13" to Files.Single("textures/metal/Metal_13-256x256.png"),
        "METAL_14" to Files.Single("textures/metal/Metal_14-256x256.png"),
        "METAL_15" to Files.Single("textures/metal/Metal_15-256x256.png"),
        "METAL_16" to Files.Single("textures/metal/Metal_16-256x256.png"),
        "METAL_17" to Files.Single("textures/metal/Metal_17-256x256.png"),
        "METAL_18" to Files.Single("textures/metal/Metal_18-256x256.png"),
        "METAL_19" to Files.Single("textures/metal/Metal_19-256x256.png"),
        "METAL_20" to Files.Single("textures/metal/Metal_20-256x256.png"),
        "TEST" to Files.Single("textures/uv.jpeg"),
        "SUNSET_BAY_SKYBOX" to Files.Multiple(
            arrayOf(
                "textures/sunset_bay_skybox/SunsetBay_E.png",
                "textures/sunset_bay_skybox/SunsetBay_W.png",
                "textures/sunset_bay_skybox/SunsetBay_U.png",
                "textures/sunset_bay_skybox/SunsetBay_D.png",
                "textures/sunset_bay_skybox/SunsetBay_N.png",
                "textures/sunset_bay_skybox/SunsetBay_S.png"
            )
        ),
        "VERY_BIG_MOUNTAINS_SKYBOX" to Files.Multiple(
            arrayOf(
                "textures/very_big_mountains_skybox/VeryBigMountainsV2_E.png",
                "textures/very_big_mountains_skybox/VeryBigMountainsV2_W.png",
                "textures/very_big_mountains_skybox/VeryBigMountainsV2_U.png",
                "textures/very_big_mountains_skybox/VeryBigMountainsV2_D.png",
                "textures/very_big_mountains_skybox/VeryBigMountainsV2_N.png",
                "textures/very_big_mountains_skybox/VeryBigMountainsV2_S.png"
            )
        ),
        "DESERT_SKYBOX" to Files.Multiple(
            arrayOf(
                "textures/desert_skybox/SkyboxDesert_E.png",
                "textures/desert_skybox/SkyboxDesert_W.png",
                "textures/desert_skybox/SkyboxDesert_U.png",
                "textures/desert_skybox/SkyboxDesert_D.png",
                "textures/desert_skybox/SkyboxDesert_N.png",
                "textures/desert_skybox/SkyboxDesert_S.png"
            )
        )
    )
)