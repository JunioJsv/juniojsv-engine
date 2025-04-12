package juniojsv.engine.features.shader

import juniojsv.engine.features.utils.IDisposable
import juniojsv.engine.features.utils.Resources
import org.lwjgl.opengl.GL32
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.Delegates

sealed class Shader(val file: String, val type: Int) : IDisposable {
    var id: Int by Delegates.notNull()

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Shader::class.java)
    }


    init {
        id = GL32.glCreateShader(type)
        compile()
    }

    private fun compile() {
        val source = Resources.text(file)

        GL32.glShaderSource(id, source)
        GL32.glCompileShader(id)

        val compiled = GL32.glGetShaderi(id, GL32.GL_COMPILE_STATUS)
        if (compiled == GL32.GL_FALSE) {
            val log = GL32.glGetShaderInfoLog(id)
            logger.error("Error compiling shader $file:\n$log\n$source")
        }
    }

    override fun dispose() {
        super.dispose()
        GL32.glDeleteShader(id)
    }

    override fun toString(): String {
        return "Shader(id=$id, file='$file')"
    }
}

class FragmentShader(file: String) : Shader(file, GL32.GL_FRAGMENT_SHADER)

class VertexShader(file: String) : Shader(file, GL32.GL_VERTEX_SHADER)

