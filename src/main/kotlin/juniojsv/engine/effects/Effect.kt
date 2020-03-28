package juniojsv.engine.effects

import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20

class Effect(vertex: Shader, fragment: Shader) {
    val id: Int = GL20.glCreateProgram()

    //    constructor(vertexFile: String, fragmentFile: String) : this() {
//        getResources(vertexFile) { stream ->
//            decode(stream) { vertexSource ->
//                getResources(fragmentFile) { stream ->
//                    decode(stream) { fragmentSource ->
//                        attachAndValidate(
//                                ShaderRaw(vertexSource, ShaderType.VERTEX).id,
//                                ShaderRaw(fragmentSource, ShaderType.FRAGMENT).id
//                        )
//                    }
//                }
//            }
//        }
//    }
    init {
        attachAndValidate(vertex.id, fragment.id)
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