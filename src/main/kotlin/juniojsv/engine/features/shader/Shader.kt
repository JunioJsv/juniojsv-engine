package juniojsv.engine.features.shader

import juniojsv.engine.features.utils.Resource
import org.lwjgl.opengl.GL32
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

class Shader(private val file: String, val type: ShaderType) {
    var id: Int by Delegates.notNull()

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
        compile(id, source)

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

private fun compile(id: Int, source: String) {
    GL32.glShaderSource(id, source)
    GL32.glCompileShader(id)
}