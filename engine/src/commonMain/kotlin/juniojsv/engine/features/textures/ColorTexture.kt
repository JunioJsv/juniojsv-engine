package juniojsv.engine.features.textures

import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.PlatformMemory
import juniojsv.engine.platforms.SupportedPlatform
import juniojsv.engine.platforms.constants.GL_CLAMP_TO_EDGE
import juniojsv.engine.platforms.constants.GL_LINEAR
import juniojsv.engine.platforms.constants.GL_RGBA
import juniojsv.engine.platforms.constants.GL_TEXTURE_2D
import juniojsv.engine.platforms.constants.GL_TEXTURE_BINDING_2D
import juniojsv.engine.platforms.constants.GL_TEXTURE_MAG_FILTER
import juniojsv.engine.platforms.constants.GL_TEXTURE_MIN_FILTER
import juniojsv.engine.platforms.constants.GL_TEXTURE_WRAP_S
import juniojsv.engine.platforms.constants.GL_TEXTURE_WRAP_T
import juniojsv.engine.platforms.constants.GL_UNSIGNED_BYTE
import juniojsv.engine.platforms.platform
import org.joml.Vector3f

class ColorTexture(
    override val width: Int,
    override val height: Int,
    color: Vector3f? = null,
    internalFormat: Int = GL_RGBA
) : Texture() {
    init {
        val type = getType()
        GL.glBindTexture(type, id)
        val pixels = color?.let {
            val buffer = PlatformMemory.alloc(width * height * 4)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    buffer.put((it.x * 255).toInt().toByte())
                    buffer.put((it.y * 255).toInt().toByte())
                    buffer.put((it.z * 255).toInt().toByte())
                    buffer.put(255.toByte())
                }
            }
            buffer.flip()
            buffer
        }

        GL.glTexParameteri(type, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        GL.glTexParameteri(type, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        GL.glTexParameteri(type, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        GL.glTexParameteri(type, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

        GL.glTexImage2D(
            type,
            0,
            if (platform == SupportedPlatform.ANDROID) GL_RGBA else internalFormat,
            width,
            height,
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            pixels
        )

        pixels?.let { PlatformMemory.free(it) }


        GL.glBindTexture(type, 0)
    }

    override fun getBindType(): Int {
        return GL_TEXTURE_BINDING_2D
    }

    override fun getType(): Int {
        return GL_TEXTURE_2D
    }
}