package juniojsv.engine.features.utils.factories

import juniojsv.engine.features.shader.FragmentShader
import juniojsv.engine.features.shader.Shaders
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.shader.VertexShader

object ShadersProgramFactory {
    private val shaders = mutableMapOf(
        "DEFAULT" to Shaders(
            VertexShader("shaders/default.vert"),
            FragmentShader("shaders/default.frag")
        ),
        "DEFAULT_INSTANCED" to Shaders(
            VertexShader("shaders/default_instanced.vert"),
            FragmentShader("shaders/default_instanced.frag")
        ),
        "DEFAULT_INSTANCED_DEBUG" to Shaders(
            VertexShader("shaders/default_instanced.vert"),
            FragmentShader("shaders/default_instanced_debug.frag")
        ),
        "WINDOW" to Shaders(
            VertexShader("shaders/window.vert"),
            FragmentShader("shaders/window.frag")
        ),
        "SKYBOX" to Shaders(
            VertexShader("shaders/skybox.vert"),
            FragmentShader("shaders/skybox.frag")
        ),
        "VELOCITY" to Shaders(
            VertexShader("shaders/velocity.vert"),
            FragmentShader("shaders/velocity.frag")
        ),
        "VELOCITY_INSTANCED" to Shaders(
            VertexShader("shaders/velocity_instanced.vert"),
            FragmentShader("shaders/velocity.frag")
        )
    )

    fun getShaders(program: String) =
        shaders[program] ?: throw IllegalArgumentException("Shader program '$program' not found")

    fun createDefault() = create("DEFAULT")
    fun createDefaultInstanced() = create("DEFAULT_INSTANCED")

    fun create(program: String): ShadersProgram {
        val shaders = shaders[program] ?: throw IllegalArgumentException("Shader program '$program' not found")
        return ShadersProgram(shaders)
    }

    fun registry(program: String, shaders: Shaders) {
        this.shaders[program] = shaders
    }

    fun registry(programs: Map<String, Shaders>) {
        for (program in programs) {
            registry(program.key, program.value)
        }
    }
}
