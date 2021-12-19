package juniojsv.engine.features.shader

import juniojsv.engine.features.utils.Resource
import org.lwjgl.opengl.GL32
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.properties.Delegates

enum class ShaderType {
    GEOMETRY,
    VERTEX,
    FRAGMENT
}

class Shader(file: String, type: ShaderType) {
    var id: Int by Delegates.notNull()

    private fun getSource(stream: InputStream, onSuccess: (source: String) -> Unit) {
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
        onSuccess(source)
    }

    init {
        id = when (type) {
            ShaderType.GEOMETRY -> GL32.glCreateShader(GL32.GL_GEOMETRY_SHADER)
            ShaderType.VERTEX -> GL32.glCreateShader(GL32.GL_VERTEX_SHADER)
            ShaderType.FRAGMENT -> GL32.glCreateShader(GL32.GL_FRAGMENT_SHADER)
        }
        Resource.getResource(file) { stream ->
            getSource(stream) { source ->
                compile(id, source)
            }
        }
    }

    private fun compile(id: Int, source: String) {
        GL32.glShaderSource(id, source)
        GL32.glCompileShader(id)
    }
}