package juniojsv.engine.features.texture

import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL32
import java.nio.ByteBuffer

class ColorTexture(width: Int, height: Int) : Texture() {
    init {
        GL32.glBindTexture(GL32.GL_TEXTURE_2D, id)
        GL32.glTexImage2D(
            GL32.GL_TEXTURE_2D,
            0,
            GL30.GL_RGBA,
            width,
            height,
            0,
            GL30.GL_RGBA,
            GL30.GL_UNSIGNED_BYTE,
            null as ByteBuffer?
        )
        GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_MIN_FILTER, GL32.GL_LINEAR)
        GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_MAG_FILTER, GL32.GL_LINEAR)
        GL32.glBindTexture(GL32.GL_TEXTURE_2D, 0)
    }
}