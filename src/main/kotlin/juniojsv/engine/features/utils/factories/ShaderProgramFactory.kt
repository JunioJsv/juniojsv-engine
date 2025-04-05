package juniojsv.engine.features.utils.factories

import juniojsv.engine.features.shader.Shader
import juniojsv.engine.features.shader.ShaderType
import juniojsv.engine.features.shader.ShadersProgram

enum class ShaderPrograms(val vertex: String, val fragment: String) {
    DEFAULT("shaders/default.vert", "shaders/default.frag"),
    DEFAULT_INSTANCED("shaders/default_instanced.vert", "shaders/default_instanced.frag"),
    DEFAULT_INSTANCED_DEBUG("shaders/default_instanced.vert", "shaders/default_instanced_debug.frag"),
    WINDOW("shaders/window.vert", "shaders/window.frag"),
    SKYBOX("shaders/skybox.vert", "shaders/skybox.frag"),
    TEST("shaders/default.vert", "shaders/test.frag"),
    TEST_INSTANCED("shaders/default_instanced.vert", "shaders/test.frag")
}

object ShaderProgramFactory {
    fun create(program: ShaderPrograms): ShadersProgram {
        return ShadersProgram(
            Shader(program.vertex, ShaderType.VERTEX),
            Shader(program.fragment, ShaderType.FRAGMENT)
        )
    }
}