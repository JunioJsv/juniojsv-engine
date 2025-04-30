package juniojsv.engine.features.textures

import juniojsv.engine.features.window.Resolution
import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.constants.*
import java.nio.ByteBuffer

class CopyFrameBufferTexture(private var resolution: Resolution) : Texture() {

    init {
        create()
    }

    private fun create() {
        val type = getType()
        GL.glBindTexture(type, id)
        GL.glTexImage2D(
            type,
            0,
            GL_RGBA,
            resolution.width,
            resolution.height,
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE, null as ByteBuffer?
        )
        GL.glTexParameteri(type, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        GL.glTexParameteri(type, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        GL.glBindTexture(type, 0)
    }

    private fun recreate() {
        dispose()
        id = GL.glGenTextures()
        create()
        update()
    }

    fun update() {
        val type = getType()
        GL.glBindTexture(type, id)
        GL.glCopyTexImage2D(type, 0, GL_RGBA, 0, 0, resolution.width, resolution.height, 0)
        GL.glBindTexture(type, 0)
    }

    fun resize(resolution: Resolution) {
        if (this.resolution == resolution) return
        this.resolution = resolution
        recreate()
    }

    override fun getBindType(): Int {
        return GL_TEXTURE_BINDING_2D
    }

    override fun getType(): Int {
        return GL_TEXTURE_2D
    }
}