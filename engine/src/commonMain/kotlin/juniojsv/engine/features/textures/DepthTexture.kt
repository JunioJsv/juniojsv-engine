package juniojsv.engine.features.textures

import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.SupportedPlatform
import juniojsv.engine.platforms.constants.GL_CLAMP_TO_BORDER
import juniojsv.engine.platforms.constants.GL_DEPTH_COMPONENT
import juniojsv.engine.platforms.constants.GL_DEPTH_COMPONENT16
import juniojsv.engine.platforms.constants.GL_DEPTH_COMPONENT32
import juniojsv.engine.platforms.constants.GL_FLOAT
import juniojsv.engine.platforms.constants.GL_NEAREST
import juniojsv.engine.platforms.constants.GL_TEXTURE_2D
import juniojsv.engine.platforms.constants.GL_TEXTURE_BINDING_2D
import juniojsv.engine.platforms.constants.GL_TEXTURE_BORDER_COLOR
import juniojsv.engine.platforms.constants.GL_TEXTURE_MAG_FILTER
import juniojsv.engine.platforms.constants.GL_TEXTURE_MIN_FILTER
import juniojsv.engine.platforms.constants.GL_TEXTURE_WRAP_S
import juniojsv.engine.platforms.constants.GL_TEXTURE_WRAP_T
import juniojsv.engine.platforms.constants.GL_UNSIGNED_SHORT
import juniojsv.engine.platforms.platform
import java.nio.ByteBuffer

class DepthTexture(override val width: Int, override val height: Int) : Texture() {
    init {
        val type = getType()
        GL.glBindTexture(type, id)

        val internalFormat = when (platform) {
            SupportedPlatform.ANDROID -> GL_DEPTH_COMPONENT16
            SupportedPlatform.JVM_WINDOWS -> GL_DEPTH_COMPONENT32
        }
        val valueType = when (platform) {
            SupportedPlatform.ANDROID -> GL_UNSIGNED_SHORT
            SupportedPlatform.JVM_WINDOWS -> GL_FLOAT
        }

        GL.glTexParameteri(type, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        GL.glTexParameteri(type, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        GL.glTexParameteri(type, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER)
        GL.glTexParameteri(type, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER)
        GL.glTexParameterfv(type, GL_TEXTURE_BORDER_COLOR, floatArrayOf(1f, 1f, 1f, 1f))

        GL.glTexImage2D(
            type,
            0,
            internalFormat,
            width, height,
            0,
            GL_DEPTH_COMPONENT,
            valueType,
            null as ByteBuffer?
        )

        GL.glBindTexture(type, 0)
    }

    override fun getBindType(): Int {
        return GL_TEXTURE_BINDING_2D
    }

    override fun getType(): Int {
        return GL_TEXTURE_2D
    }
}