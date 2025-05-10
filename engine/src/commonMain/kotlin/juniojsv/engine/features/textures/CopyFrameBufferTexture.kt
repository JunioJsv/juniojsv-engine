package juniojsv.engine.features.textures

import juniojsv.engine.features.window.Resolution
import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.constants.GL_LINEAR
import juniojsv.engine.platforms.constants.GL_RGBA
import juniojsv.engine.platforms.constants.GL_TEXTURE_2D
import juniojsv.engine.platforms.constants.GL_TEXTURE_BINDING_2D
import juniojsv.engine.platforms.constants.GL_TEXTURE_MAG_FILTER
import juniojsv.engine.platforms.constants.GL_TEXTURE_MIN_FILTER
import juniojsv.engine.platforms.constants.GL_UNSIGNED_BYTE
import java.nio.ByteBuffer

class CopyFrameBufferTexture(override var width: Int, override var height: Int) : Texture() {

    init {
        create()
    }

    private fun create() {
        val type = getType()
        GL.glBindTexture(type, id)

        GL.glTexParameteri(type, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        GL.glTexParameteri(type, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        GL.glTexImage2D(
            type,
            0,
            GL_RGBA,
            width,
            height,
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE, null as ByteBuffer?
        )

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
        GL.glCopyTexImage2D(type, 0, GL_RGBA, 0, 0, width, height, 0)
        GL.glBindTexture(type, 0)
    }

    fun resize(resolution: Resolution) {
        if (resolution.width == width && resolution.height == height) return
        width = resolution.width
        height = resolution.height
        recreate()
    }

    override fun getBindType(): Int {
        return GL_TEXTURE_BINDING_2D
    }

    override fun getType(): Int {
        return GL_TEXTURE_2D
    }
}