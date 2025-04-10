package juniojsv.engine.features.texture

import juniojsv.engine.features.window.Resolution
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL32
import java.nio.ByteBuffer

class CopyFrameBufferTexture(private var resolution: Resolution) : Texture() {

    init {
        create()
    }

    private fun create() {
        val type = getType()
        GL32.glBindTexture(type, id)
        GL32.glTexImage2D(
            type,
            0,
            GL30.GL_RGBA,
            resolution.width,
            resolution.height,
            0,
            GL30.GL_RGBA,
            GL30.GL_UNSIGNED_BYTE, null as ByteBuffer?
        )
        GL32.glTexParameteri(type, GL32.GL_TEXTURE_MIN_FILTER, GL32.GL_LINEAR)
        GL32.glTexParameteri(type, GL32.GL_TEXTURE_MAG_FILTER, GL32.GL_LINEAR)
        GL32.glBindTexture(type, 0)
    }

    private fun recreate() {
        dispose()
        id = GL11.glGenTextures()
        create()
        update()
    }

    fun update() {
        val type = getType()
        GL30.glBindTexture(type, id)
        GL30.glCopyTexImage2D(type, 0, GL11.GL_RGBA, 0, 0, resolution.width, resolution.height, 0)
        GL32.glBindTexture(type, 0)
    }

    fun resize(resolution: Resolution) {
        if (this.resolution == resolution) return
        this.resolution = resolution
        recreate()
    }

    override fun getBindType(): Int {
        return GL30.GL_TEXTURE_BINDING_2D
    }

    override fun getType(): Int {
        return GL11.GL_TEXTURE_2D
    }
}