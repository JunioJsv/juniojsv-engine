package juniojsv.engine.features.utils.factories

import juniojsv.engine.features.shader.Shaders
import juniojsv.engine.features.shader.ShadersProgram

object ShadersProgramFactory {
    private val shaders = mutableMapOf<String, Shaders>()

    fun getShadersOf(program: String) =
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
