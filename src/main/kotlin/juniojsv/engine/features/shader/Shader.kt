package juniojsv.engine.features.shader

import juniojsv.engine.features.utils.Resource
import org.lwjgl.opengl.GL32
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.Executors
import kotlin.properties.Delegates

enum class ShaderType {
    GEOMETRY,
    VERTEX,
    FRAGMENT
}

class Shader(val file: String, val type: ShaderType) {
    var id: Int by Delegates.notNull()

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Shader::class.java)
    }

    private fun getSource(stream: InputStream): String = Executors.newSingleThreadExecutor().submit<String> {
        var source = String()
        BufferedReader(InputStreamReader(stream)).also { file ->
            while (true) {
                file.readLine()?.also { line ->
                    source += "$line\n"
                } ?: break
            }
            file.close()
        }
        stream.close()
        source
    }.get()


    init {
        id = when (type) {
            ShaderType.GEOMETRY -> GL32.glCreateShader(GL32.GL_GEOMETRY_SHADER)
            ShaderType.VERTEX -> GL32.glCreateShader(GL32.GL_VERTEX_SHADER)
            ShaderType.FRAGMENT -> GL32.glCreateShader(GL32.GL_FRAGMENT_SHADER)
        }
        val source = Resource.get(file).let(::getSource)
        compile(id, source, file)

    }

    private fun compile(id: Int, source: String, path: String = "") {
        GL32.glShaderSource(id, source)
        GL32.glCompileShader(id)

        val compiled = GL32.glGetShaderi(id, GL32.GL_COMPILE_STATUS)
        if (compiled == GL32.GL_FALSE) {
            val log = GL32.glGetShaderInfoLog(id)
            logger.error("Error compiling shader $path:\n$log")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Shader

        if (file != other.file) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = file.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

}

