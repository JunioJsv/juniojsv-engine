package juniojsv.engine

import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20
import java.io.BufferedReader
import java.io.InputStreamReader

abstract class Shader {
    abstract var id: Int
    abstract var vertex: Int
    abstract var fragment: Int

    abstract fun putUniform(name: String, value: Any)

    companion object {
        fun fromRaw(vertexSrc: String, fragmentSrc: String): Shader {
            return object : Shader() {
                override var id: Int = GL20.glCreateProgram()
                override var vertex: Int = GL20.glCreateShader(GL20.GL_VERTEX_SHADER)
                override var fragment: Int = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER)

                override fun putUniform(name: String, value: Any) {
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

                init {
                    GL20.glShaderSource(vertex, vertexSrc)
                    GL20.glCompileShader(vertex)

                    GL20.glShaderSource(fragment, fragmentSrc)
                    GL20.glCompileShader(fragment)

                    GL20.glAttachShader(id, vertex)
                    GL20.glAttachShader(id, fragment)

                    GL20.glBindAttribLocation(id, 0, "vertice_position")
                    GL20.glBindAttribLocation(id, 1, "uv_coordinates")
                    GL20.glBindAttribLocation(id, 2, "vertice_normal")

                    GL20.glLinkProgram(id)
                    GL20.glValidateProgram(id)
                }
            }
        }

        fun fromResources(vertName: String, fragName: String): Shader {
            // [0] vertex shader [1] fragment shader
            val buffer: Array<String> = arrayOf(
                String(),
                String()
            )

            Shader::class.java.classLoader.also { loader ->
                repeat(2) { index ->
                    loader.getResourceAsStream(
                        if (index == 0) "$vertName.vert"
                        else "$fragName.frag"
                    )?.let { src ->
                        BufferedReader(InputStreamReader(src)).also { shader ->
                            while (true) {
                                shader.readLine()?.also { line ->
                                    buffer[index] += "$line\n"
                                } ?: break
                            }
                            shader.close()
                        }
                        src.close()
                    }
                }
            }
            return fromRaw(buffer[0], buffer[1])
        }
    }
}