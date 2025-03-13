package juniojsv.engine.features.utils.factories

import juniojsv.engine.features.shader.Shader
import juniojsv.engine.features.shader.ShaderType
import juniojsv.engine.features.shader.ShadersProgram

enum class ShaderPrograms {
    DEFAULT,
    DEFAULT_INSTANCED,
    SCREEN,
    SKYBOX
}

object ShaderProgramFactory {
    private val programs: Map<ShaderPrograms, () -> ShadersProgram> = mapOf(
        ShaderPrograms.DEFAULT to {
            ShadersProgram(
                Shader("shaders/default.vert", ShaderType.VERTEX),
                Shader("shaders/default.frag", ShaderType.FRAGMENT)
            )
        },
        ShaderPrograms.DEFAULT_INSTANCED to {
            ShadersProgram(
                Shader("shaders/default_instanced.vert", ShaderType.VERTEX),
                Shader("shaders/default_instanced.frag", ShaderType.FRAGMENT)
            )
        },
        ShaderPrograms.SCREEN to {
            ShadersProgram(
                Shader("shaders/screen.vert", ShaderType.VERTEX),
                Shader("shaders/screen.frag", ShaderType.FRAGMENT)
            )
        },
        ShaderPrograms.SKYBOX to {
            ShadersProgram(
                Shader("shaders/skybox.vert", ShaderType.VERTEX),
                Shader("shaders/skybox.frag", ShaderType.FRAGMENT)
            )
        }
    )

    fun create(program: ShaderPrograms) = programs[program]!!.invoke()
}