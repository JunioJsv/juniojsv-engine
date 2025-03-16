package juniojsv.engine.features.texture

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL32

class DepthTexture(width: Int, height: Int) : Texture() {
    init {
        val type = getTextureType()
        GL32.glBindTexture(type, id)
        GL32.glTexImage2D(
            type,
            0,
            GL32.GL_DEPTH_COMPONENT32,
            width, height,
            0,
            GL32.GL_DEPTH_COMPONENT,
            GL32.GL_FLOAT,
            0
        )
        GL32.glTexParameteri(type, GL32.GL_TEXTURE_MIN_FILTER, GL32.GL_NEAREST)
        GL32.glTexParameteri(type, GL32.GL_TEXTURE_MAG_FILTER, GL32.GL_NEAREST)
        GL32.glTexParameteri(type, GL32.GL_TEXTURE_WRAP_S, GL32.GL_CLAMP_TO_EDGE)
        GL32.glTexParameteri(type, GL32.GL_TEXTURE_WRAP_T, GL32.GL_CLAMP_TO_EDGE)
        GL32.glBindTexture(type, 0)
    }

    override fun getTextureBindType(): Int {
        return GL30.GL_TEXTURE_BINDING_2D
    }

    override fun getTextureType(): Int {
        return GL11.GL_TEXTURE_2D
    }
}