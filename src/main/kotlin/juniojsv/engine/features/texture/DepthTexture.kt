package juniojsv.engine.features.texture

import org.lwjgl.opengl.GL32

class DepthTexture(width: Int, height: Int) : Texture() {
    init {
        GL32.glBindTexture(GL32.GL_TEXTURE_2D, id)
        GL32.glTexImage2D(
            GL32.GL_TEXTURE_2D,
            0,
            GL32.GL_DEPTH_COMPONENT32,
            width, height,
            0,
            GL32.GL_DEPTH_COMPONENT,
            GL32.GL_FLOAT,
            0
        )
        GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_MIN_FILTER, GL32.GL_NEAREST)
        GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_MAG_FILTER, GL32.GL_NEAREST)
        GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_WRAP_S, GL32.GL_CLAMP_TO_EDGE)
        GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_WRAP_T, GL32.GL_CLAMP_TO_EDGE)
        GL32.glBindTexture(GL32.GL_TEXTURE_2D, 0)
    }
}