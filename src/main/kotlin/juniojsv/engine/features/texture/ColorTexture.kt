package juniojsv.engine.features.texture

import org.joml.Vector3f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL32
import org.lwjgl.system.MemoryUtil


class ColorTexture(width: Int, height: Int, color: Vector3f? = null) : Texture() {
    init {
        val type = getTextureType()
        GL32.glBindTexture(type, id)
        val pixels = color?.let {
            val buffer = MemoryUtil.memAlloc(width * height * 4)
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
        GL32.glTexImage2D(
            type,
            0,
            GL30.GL_RGBA,
            width,
            height,
            0,
            GL30.GL_RGBA,
            GL30.GL_UNSIGNED_BYTE,
            pixels
        )

        pixels?.let { MemoryUtil.memFree(it) }

        GL32.glTexParameteri(type, GL32.GL_TEXTURE_MIN_FILTER, GL32.GL_LINEAR)
        GL32.glTexParameteri(type, GL32.GL_TEXTURE_MAG_FILTER, GL32.GL_LINEAR)
        GL32.glBindTexture(type, 0)
    }

    override fun getTextureBindType(): Int {
        return GL30.GL_TEXTURE_BINDING_2D
    }

    override fun getTextureType(): Int {
        return GL11.GL_TEXTURE_2D
    }
}
