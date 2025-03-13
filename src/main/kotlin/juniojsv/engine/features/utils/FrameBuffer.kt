package juniojsv.engine.features.utils

import juniojsv.engine.features.texture.ColorTexture
import juniojsv.engine.features.texture.DepthTexture
import juniojsv.engine.features.window.Resolution
import juniojsv.engine.features.window.Window
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL32
import kotlin.properties.Delegates

class FrameBuffer(
    private val window: Window,
    private var resolution: Resolution,
    private val depth: Boolean = false,
    private val color: Boolean = false
) {
    private var fbo by Delegates.notNull<Int>()
    var depthTexture: DepthTexture? = null
    var colorTexture: ColorTexture? = null

    init {
        create()
    }

    private fun create() {
        fbo = GL32.glGenFramebuffers()
        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, fbo)

        if (depth) {
            depthTexture = DepthTexture(resolution.width, resolution.height)
            GL32.glFramebufferTexture2D(
                GL32.GL_FRAMEBUFFER,
                GL32.GL_DEPTH_ATTACHMENT,
                GL32.GL_TEXTURE_2D,
                depthTexture!!.id,
                0
            )
        }
        if (depth && !color) {
            GL32.glDrawBuffers(GL32.GL_NONE)
            GL32.glReadBuffer(GL32.GL_NONE)
        }
        if (color) {
            colorTexture = ColorTexture(resolution.width, resolution.height)
            GL32.glFramebufferTexture2D(
                GL32.GL_FRAMEBUFFER,
                GL30.GL_COLOR_ATTACHMENT0,
                GL32.GL_TEXTURE_2D,
                colorTexture!!.id,
                0
            )
            if (!depth) {
                val attachments = intArrayOf(GL30.GL_COLOR_ATTACHMENT0)
                GL30.glDrawBuffers(attachments)
            }
        }

        if (GL32.glCheckFramebufferStatus(GL32.GL_FRAMEBUFFER) != GL32.GL_FRAMEBUFFER_COMPLETE) {
            throw RuntimeException("Frame buffer is not complete!")
        }

        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, 0)
    }

    fun bind() {
        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, fbo)
        GL32.glViewport(0, 0, resolution.width, resolution.height)
        if (depth)
            GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT)
        if (color)
            GL30.glClear(GL11.GL_COLOR_BUFFER_BIT)
    }

    fun unbind() {
        val viewport = window.getResolution()
        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, 0)
        GL32.glViewport(0, 0, viewport.width, viewport.height)
    }

    fun dispose() {
        GL32.glDeleteFramebuffers(fbo)
        depthTexture?.dispose()
        colorTexture?.dispose()
        depthTexture = null
        colorTexture = null
    }

    fun resize(resolution: Resolution) {
        this.resolution = resolution
        dispose()
        create()
    }
}
