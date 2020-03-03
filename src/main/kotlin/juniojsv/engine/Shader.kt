package juniojsv.engine

import juniojsv.engine.utils.Resource
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL33
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

abstract class Shader : Resource() {
    val id: Int = GL20.glCreateProgram()

    enum class ShaderType {
        GEOMETRY,
        VERTEX,
        FRAGMENT
    }

    protected fun decode(stream: InputStream, onSuccess: (source: String) -> Unit) {
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

    fun putUniform(name: String, value: Any) {
        GL20.glGetUniformLocation(id, name).also { location ->
            when (value) {
                is Int ->
                    GL20.glUniform1i(location, value)
                is Float ->
                    GL20.glUniform1f(location, value)
                is Vector3f ->
                    BufferUtils.createFloatBuffer(3).also { vec3 ->
                        value.get(vec3)
                        GL20.glUniform3fv(location, vec3)
                    }
                is Matrix4f ->
                    BufferUtils.createFloatBuffer(16).also { matrix4f ->
                        value.get(matrix4f)
                        GL20.glUniformMatrix4fv(
                            location,
                            false,
                            matrix4f
                        )
                    }
                else -> throw Exception("Uniform type don't implemented")
            }
        }
    }

    data class ShaderRaw(val source: String, val type: ShaderType) {
        var id = 0
            private set

        init {
            id = when (type) {
                ShaderType.GEOMETRY -> GL20.glCreateShader(GL33.GL_GEOMETRY_SHADER)
                ShaderType.VERTEX -> GL20.glCreateShader(GL20.GL_VERTEX_SHADER)
                ShaderType.FRAGMENT -> GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER)
            }
            compile(id, source)
        }

        private fun compile(id: Int, source: String) {
            GL20.glShaderSource(id, source)
            GL20.glCompileShader(id)
        }
    }

    class Program internal constructor() : Shader() {
        constructor(vertexRaw: ShaderRaw, fragmentRaw: ShaderRaw) : this() {
            attachAndValidate(vertexRaw.id, fragmentRaw.id)
        }

        constructor(vertexFile: String, fragmentFile: String) : this() {
            getResources(vertexFile) { stream ->
                decode(stream) { vertexSource ->
                    getResources(fragmentFile) { stream ->
                        decode(stream) { fragmentSource ->
                            attachAndValidate(
                                ShaderRaw(vertexSource, ShaderType.VERTEX).id,
                                ShaderRaw(fragmentSource, ShaderType.FRAGMENT).id
                            )
                        }
                    }
                }
            }
        }

        private fun attachAndValidate(vertexId: Int, fragmentId: Int) {
            GL20.glAttachShader(id, vertexId)
            GL20.glAttachShader(id, fragmentId)

            GL20.glBindAttribLocation(id, 0, "vertex_position")
            GL20.glBindAttribLocation(id, 1, "uv_coordinates")
            GL20.glBindAttribLocation(id, 2, "vertex_normal")

            GL20.glLinkProgram(id)
            GL20.glValidateProgram(id)
        }
    }
}