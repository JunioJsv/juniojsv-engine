package juniojsv.engine.constants

import juniojsv.engine.Shader

val DEFAULT_SHADER by lazy {
    Shader.fromResources("shaders/default", "shaders/default")
}
val DEPTH_SHADER by lazy {
    Shader.fromResources("shaders/default", "shaders/depth")
}