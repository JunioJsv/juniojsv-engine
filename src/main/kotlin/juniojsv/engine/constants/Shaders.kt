package juniojsv.engine.constants

import juniojsv.engine.Shader

val DEFAULT_SHADER by lazy {
    Shader.fromResources("default", "default")
}