package juniojsv.engine

import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20
import java.io.BufferedReader
import java.io.InputStreamReader

abstract class Shader {
    abstract var identifier: Int
    abstract var vertex: Int
    abstract var fragment: Int

    abstract fun putUniform(name: String, value: Any)

    companion object {
        fun fromRaw(vertShader: String, fragShader: String): Shader {
            return object : Shader() {
                override var identifier: Int = GL20.glCreateProgram()
                override var vertex: Int = GL20.glCreateShader(GL20.GL_VERTEX_SHADER)
                override var fragment: Int = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER)

                override fun putUniform(name: String, value: Any) {
                    GL20.glGetUniformLocation(identifier, name).also { location ->
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
                    GL20.glShaderSource(vertex, vertShader)
                    GL20.glCompileShader(vertex)

                    GL20.glShaderSource(fragment, fragShader)
                    GL20.glCompileShader(fragment)

                    GL20.glAttachShader(identifier, vertex)
                    GL20.glAttachShader(identifier, fragment)

                    GL20.glBindAttribLocation(identifier, 0, "position")
                    GL20.glBindAttribLocation(identifier, 2, "normal")

                    GL20.glLinkProgram(identifier)
                    GL20.glValidateProgram(identifier)
                }
            }
        }

        fun fromResources(vertName: String, fragName: String): Shader {
            var vertShader = ""
            var fragShader = ""
            var buffer: String?

            Shader::class.java.classLoader.also { loader ->
                loader.getResourceAsStream("$vertName.vert")?.let { vertex ->
                    BufferedReader(InputStreamReader(vertex)).also { shader ->
                        while (true) {
                            buffer = shader.readLine()
                            if (buffer != null)
                                vertShader += "$buffer\n"
                            else break
                        }
                        shader.close()
                    }
                    vertex.close()
                }
                loader.getResourceAsStream("$fragName.frag")?.let { fragment ->
                    BufferedReader(InputStreamReader(fragment)).also { shader ->
                        while (true) {
                            buffer = shader.readLine()
                            if (buffer != null)
                                fragShader += "$buffer\n"
                            else break
                        }
                        shader.close()
                    }
                    fragment.close()
                }
            }

            return fromRaw(vertShader, fragShader)
        }
    }
}