package juniojsv.engine.features.texture

object TestTexture : TwoDimensionTexture("textures/uv.jpeg")

object TerrainTexture : TwoDimensionTexture("textures/ground.jpg")

object SkyboxTexture : CubeMapTexture(
    arrayOf(
        "textures/sunset_bay_skybox/SunsetBay_E.png",
        "textures/sunset_bay_skybox/SunsetBay_W.png",
        "textures/sunset_bay_skybox/SunsetBay_U.png",
        "textures/sunset_bay_skybox/SunsetBay_D.png",
        "textures/sunset_bay_skybox/SunsetBay_N.png",
        "textures/sunset_bay_skybox/SunsetBay_S.png"
    )
)