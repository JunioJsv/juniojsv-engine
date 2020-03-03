package juniojsv.engine.constants

import juniojsv.engine.Shader

val DEFAULT_SHADER by lazy {
    Shader.Program("shaders/default.vert", "shaders/default.frag")
}
val DEPTH_SHADER by lazy {
    Shader.Program("shaders/default.vert", "shaders/depth.frag")
}
val SKYBOX_SHADER by lazy {
    Shader.Program("shaders/skybox.vert", "shaders/skybox.frag")
}