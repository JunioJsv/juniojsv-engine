package juniojsv.engine.features.shader

import juniojsv.engine.extensions.toBuffer
import juniojsv.engine.features.texture.Texture
import juniojsv.engine.features.texture.TextureUnits
import juniojsv.engine.features.utils.IDisposable
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.GL20
import org.lwjgl.system.MemoryUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class Shaders(
    val vertex: VertexShader,
    val fragment: FragmentShader
) {
    val ids = listOf(vertex.id, fragment.id)

    override fun toString(): String {
        return """Shaders(
            |   vertex=$vertex,
            |   fragment=$fragment
            |)""".trimMargin()
    }
}

class ShadersProgram : IDisposable {
    val id: Int

    constructor(shaders: Shaders) {
        id = GL20.glCreateProgram()
        setup(shaders)
    }

    constructor(id: Int) {
        this.id = id
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ShadersProgram::class.java)

        fun getCurrentShaderProgramId(): Int {
            return GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM)
        }

        fun putUniform(id: Int, name: String, value: Any) {
            if (id == 0) return
            GL20.glGetUniformLocation(id, name).also { location ->
                if (location == -1) return
                when (value) {
                    is Int ->
                        GL20.glUniform1i(location, value)

                    is Float ->
                        GL20.glUniform1f(location, value)

                    is Vector3f -> {
                        val buffer = floatArrayOf(value.x, value.y, value.z).toBuffer()
                        GL20.glUniform3fv(location, buffer)
                        MemoryUtil.memFree(buffer)
                    }

                    is Vector2f -> {
                        val buffer = floatArrayOf(value.x, value.y).toBuffer()
                        GL20.glUniform2fv(location, buffer)
                        MemoryUtil.memFree(buffer)
                    }

                    is Matrix4f -> {
                        val buffer = value.toBuffer()
                        GL20.glUniformMatrix4fv(
                            location,
                            false,
                            buffer
                        )
                        MemoryUtil.memFree(buffer)
                    }

                    is IntArray ->
                        GL20.glUniform1iv(location, value)

                    is Texture ->
                        TextureUnits.bind(value, name)

                    else -> logger.error("Uniform($value) type don't implemented")
                }
            }
        }

        fun putUniform(name: String, value: Any) = putUniform(getCurrentShaderProgramId(), name, value)

        fun bind(id: Int) {
            if (id == getCurrentShaderProgramId()) return
            GL20.glUseProgram(id)
        }
    }


    fun putUniform(name: String, value: Any) = Companion.putUniform(id, name, value)

    fun bind() = Companion.bind(id)

    private fun setup(shaders: Shaders) {
        shaders.ids.forEach { GL20.glAttachShader(id, it) }
        GL20.glLinkProgram(id)

        val linked = GL20.glGetProgrami(id, GL20.GL_LINK_STATUS)
        if (linked == GL20.GL_FALSE) {
            val log = GL20.glGetProgramInfoLog(id)
            logger.error(
                """Error linking Shader Program:
                |   $shaders
                |   $log
            """.trimMargin()
            )
        }

        GL20.glValidateProgram(id)
    }

    override fun dispose() {
        super.dispose()
        GL20.glDeleteProgram(id)
    }
}