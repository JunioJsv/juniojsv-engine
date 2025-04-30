package juniojsv.engine.features.textures

import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.SupportedPlatform
import juniojsv.engine.platforms.constants.*
import juniojsv.engine.platforms.platform
import java.nio.ByteBuffer

class DepthTexture(width: Int, height: Int) : Texture() {
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
        GL.glTexParameteri(type, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        GL.glTexParameteri(type, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        GL.glTexParameteri(type, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER)
        GL.glTexParameteri(type, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER)
        GL.glTexParameterfv(type, GL_TEXTURE_BORDER_COLOR, floatArrayOf(1f, 1f, 1f, 1f))
        GL.glBindTexture(type, 0)
    }

    override fun getBindType(): Int {
        return GL_TEXTURE_BINDING_2D
    }

    override fun getType(): Int {
        return GL_TEXTURE_2D
    }
}