package juniojsv.engine.features.shader

import juniojsv.engine.extensions.toBuffer
import juniojsv.engine.features.textures.Texture
import juniojsv.engine.features.textures.TextureUnits
import juniojsv.engine.features.utils.IDisposable
import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.PlatformMemory
import juniojsv.engine.platforms.constants.GL_CURRENT_PROGRAM
import juniojsv.engine.platforms.constants.GL_FALSE
import juniojsv.engine.platforms.constants.GL_LINK_STATUS
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
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
        id = GL.glCreateProgram()
        setup(shaders)
    }

    constructor(id: Int) {
        this.id = id
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ShadersProgram::class.java)

        fun getCurrentShaderProgramId(): Int {
            return GL.glGetInteger(GL_CURRENT_PROGRAM)
        }

        fun putUniform(id: Int, name: String, value: Any) {
            if (id == 0) return
            GL.glGetUniformLocation(id, name).also { location ->
                if (location == -1) return
                when (value) {
                    is Int ->
                        GL.glUniform1i(location, value)

                    is Float ->
                        GL.glUniform1f(location, value)

                    is Vector3f -> {
                        val buffer = floatArrayOf(value.x, value.y, value.z).toBuffer()
                        GL.glUniform3fv(location, buffer)
                        PlatformMemory.free(buffer)
                    }

                    is Vector2f -> {
                        val buffer = floatArrayOf(value.x, value.y).toBuffer()
                        GL.glUniform2fv(location, buffer)
                        PlatformMemory.free(buffer)
                    }

                    is Matrix4f -> {
                        val buffer = value.toBuffer()
                        GL.glUniformMatrix4fv(
                            location,
                            false,
                            buffer
                        )
                        PlatformMemory.free(buffer)
                    }

                    is IntArray -> {
                        val buffer = value.toBuffer()
                        GL.glUniform1iv(location, buffer)
                        PlatformMemory.free(buffer)
                    }

                    is Boolean ->
                        GL.glUniform1i(location, if (value) 1 else 0)

                    is Texture ->
                        TextureUnits.bind(value, name)

                    else -> logger.error("Uniform($value) type don't implemented")
                }
            }
        }

        fun putUniform(name: String, value: Any) = putUniform(getCurrentShaderProgramId(), name, value)

        fun bind(id: Int) {
            if (id == getCurrentShaderProgramId()) return
            GL.glUseProgram(id)
        }
    }


    fun putUniform(name: String, value: Any) = Companion.putUniform(id, name, value)

    fun bind() = bind(id)

    private fun setup(shaders: Shaders) {
        shaders.ids.forEach { GL.glAttachShader(id, it) }

        GL.glBindAttribLocation(id, 0, "aPosition")
        GL.glBindAttribLocation(id, 1, "aUV")
        GL.glBindAttribLocation(id, 2, "aNormal")

        GL.glLinkProgram(id)

        val linked = GL.glGetProgrami(id, GL_LINK_STATUS)
        if (linked == GL_FALSE) {
            val log = GL.glGetProgramInfoLog(id)
            logger.error(
                """Error linking Shader Program:
                |   $shaders
                |   $log
            """.trimMargin()
            )
        }

        GL.glValidateProgram(id)
    }

    override fun dispose() {
        super.dispose()
        GL.glDeleteProgram(id)
    }
}