package juniojsv.engine.features.shader

import juniojsv.engine.features.utils.IDisposable
import juniojsv.engine.features.utils.Resources
import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.constants.GL_COMPILE_STATUS
import juniojsv.engine.platforms.constants.GL_FALSE
import juniojsv.engine.platforms.constants.GL_FRAGMENT_SHADER
import juniojsv.engine.platforms.constants.GL_VERTEX_SHADER
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.properties.Delegates

sealed class Shader(val file: String, val type: Int) : IDisposable {
    var id: Int by Delegates.notNull()

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Shader::class.java)
    }

    init {
        id = GL.glCreateShader(type)
        compile()
    }

    private fun compile() {
        val directory = File(file).parent
        var source = Resources.text(file)

        val includeRegex = Regex("""#include\s*[<"]([^">]+)[>"]""")
        val includes = includeRegex.findAll(source).toList()
        if (includes.isNotEmpty()) {
            val includesSources = mutableListOf<String>()
            includes.forEach { matchResult ->
                val includeFile = matchResult.groupValues[1]
                val includePath = directory?.let { File(it, includeFile).invariantSeparatorsPath } ?: includeFile
                val includeSource = Resources.text(includePath)
                includesSources.add(includeSource)
            }
            source = includeRegex.replace(source) {
                includesSources.removeAt(0)
            }

        }

        GL.glShaderSource(id, source)
        GL.glCompileShader(id)

        val compiled = GL.glGetShaderi(id, GL_COMPILE_STATUS)
        if (compiled == GL_FALSE) {
            val log = GL.glGetShaderInfoLog(id)
            val source = GL.glGetShaderSource(id)
            logger.error("Error compiling $file:\n$log\n\n$source")
        }
    }

    override fun dispose() {
        super.dispose()
        GL.glDeleteShader(id)
    }

    override fun toString(): String {
        return "Shader(id=$id, file='$file')"
    }
}

class FragmentShader(file: String) : Shader(file, GL_FRAGMENT_SHADER)

class VertexShader(file: String) : Shader(file, GL_VERTEX_SHADER)
