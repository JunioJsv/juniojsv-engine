package juniojsv.engine.features.shader

object ScreenProgram : ShadersProgram(
    Shader("shaders/screen.vert", ShaderType.VERTEX),
    Shader("shaders/screen.frag", ShaderType.FRAGMENT)
)


object LightShaderProgram : ShadersProgram(
    Shader("shaders/default.vert", ShaderType.VERTEX),
    Shader("shaders/light.frag", ShaderType.FRAGMENT)
)

object SkyboxShaderProgram : ShadersProgram(
    Shader("shaders/skybox.vert", ShaderType.VERTEX),
    Shader("shaders/skybox.frag", ShaderType.FRAGMENT)
)

object DefaultShaderProgram : ShadersProgram(
    Shader("shaders/default.vert", ShaderType.VERTEX),
    Shader("shaders/default.frag", ShaderType.FRAGMENT)
)

object DefaultInstancedShaderProgram : ShadersProgram(
    Shader(
        "shaders/default_instanced.vert",
        ShaderType.VERTEX
    ),
    Shader(
        "shaders/default_instanced.frag",
        ShaderType.FRAGMENT
    )
)

object WhiteShaderProgram : ShadersProgram(
    Shader(
        "shaders/default.vert",
        ShaderType.VERTEX
    ),
    Shader(
        "shaders/white.frag",
        ShaderType.FRAGMENT
    )
)