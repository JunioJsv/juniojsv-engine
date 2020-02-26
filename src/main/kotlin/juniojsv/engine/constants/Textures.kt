package juniojsv.engine.constants

import juniojsv.engine.CubeMap
import juniojsv.engine.Texture

val UV_TEXTURE by lazy {
    Texture.fromResource("textures/uv.jpeg")
}
val GROUND_TEXTURE by lazy {
    Texture.fromResource("textures/ground.jpg")
}
val SKYBOX_DEFAULT_CUBEMAP by lazy {
    CubeMap.fromResource(
        arrayOf(
            "textures/right.png",
            "textures/left.png",
            "textures/top.png",
            "textures/bottom.png",
            "textures/front.png",
            "textures/back.png"
        )
    )
}