package juniojsv.engine.features.shader

import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20

open class ShadersProgram(vararg shaders: Shader) {
    val id: Int = GL20.glCreateProgram()

    init {
        setup(
            shaders.first { it.type == ShaderType.VERTEX },
            shaders.first { it.type == ShaderType.FRAGMENT }
        )
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

    private fun setup(vertex: Shader, fragment: Shader) {
        GL20.glAttachShader(id, vertex.id)
        GL20.glAttachShader(id, fragment.id)

        GL20.glBindAttribLocation(id, 0, VERTEX_POSITION)
        GL20.glBindAttribLocation(id, 1, UV_COORDINATE)
        GL20.glBindAttribLocation(id, 2, VERTEX_NORMAL)

        GL20.glLinkProgram(id)
        GL20.glValidateProgram(id)
    }


    companion object {
        const val VERTEX_POSITION = "vertex_position"
        const val UV_COORDINATE = "uv_coordinates"
        const val VERTEX_NORMAL = "vertex_normal"
    }
}