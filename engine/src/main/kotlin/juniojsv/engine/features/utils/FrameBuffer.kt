package juniojsv.engine.features.utils

import juniojsv.engine.features.texture.ColorTexture
import juniojsv.engine.features.texture.DepthTexture
import juniojsv.engine.features.texture.Texture
import juniojsv.engine.features.window.Resolution
import juniojsv.engine.features.window.Window
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL32
import kotlin.properties.Delegates

class FrameBuffer(
    private val window: Window,
    private var resolution: Resolution,
    attachments: Set<Int> = setOf(GL32.GL_COLOR_ATTACHMENT0, GL32.GL_DEPTH_ATTACHMENT),
    private val colorInternalFormat: Int = GL30.GL_RGBA
) {
    private var fbo by Delegates.notNull<Int>()

    private val hasColorAttachment = attachments.contains(GL32.GL_COLOR_ATTACHMENT0)
    private val hasDepthAttachment = attachments.contains(GL32.GL_DEPTH_ATTACHMENT)

    private val textures = mutableMapOf<Int, Texture>()

    init {
        create()
    }

    fun getDepthTexture(): DepthTexture {
        return textures.getOrPut(GL32.GL_DEPTH_ATTACHMENT) {
            DepthTexture(resolution.width, resolution.height)
        } as DepthTexture
    }

    fun getColorTexture(): ColorTexture {
        return textures.getOrPut(GL32.GL_COLOR_ATTACHMENT0) {
            ColorTexture(
                resolution.width,
                resolution.height,
                internalFormat = colorInternalFormat
            )
        } as ColorTexture
    }

    fun copy(
        source: FrameBuffer,
        attachments: Set<Int> = setOf(GL32.GL_COLOR_ATTACHMENT0, GL32.GL_DEPTH_ATTACHMENT)
    ) {
        GL32.glBindFramebuffer(GL32.GL_READ_FRAMEBUFFER, source.fbo)
        GL32.glBindFramebuffer(GL32.GL_DRAW_FRAMEBUFFER, fbo)
        if (attachments.contains(GL32.GL_COLOR_ATTACHMENT0) && source.hasColorAttachment)
            GL32.glBlitFramebuffer(
                0, 0, source.resolution.width, source.resolution.height,
                0, 0, resolution.width, resolution.height,
                GL32.GL_COLOR_BUFFER_BIT, GL32.GL_LINEAR
            )
        if (attachments.contains(GL32.GL_DEPTH_ATTACHMENT) && source.hasDepthAttachment)
            GL32.glBlitFramebuffer(
                0, 0, source.resolution.width, source.resolution.height,
                0, 0, resolution.width, resolution.height,
                GL32.GL_DEPTH_BUFFER_BIT, GL32.GL_NEAREST
            )
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0)
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0)
    }

    private fun create() {
        fbo = GL32.glGenFramebuffers()
        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, fbo)

        if (hasDepthAttachment) {
            val texture = getDepthTexture()
            GL32.glFramebufferTexture2D(
                GL32.GL_FRAMEBUFFER,
                GL32.GL_DEPTH_ATTACHMENT,
                GL32.GL_TEXTURE_2D,
                texture.id,
                0
            )

            if (!hasColorAttachment) {
                GL32.glDrawBuffers(GL32.GL_NONE)
                GL32.glReadBuffer(GL32.GL_NONE)
            }
        }

        if (hasColorAttachment) {
            val texture = getColorTexture()
            GL32.glFramebufferTexture2D(
                GL32.GL_FRAMEBUFFER,
                GL30.GL_COLOR_ATTACHMENT0,
                GL32.GL_TEXTURE_2D,
                texture.id,
                0
            )

            if (!hasDepthAttachment) {
                val attachments = intArrayOf(GL30.GL_COLOR_ATTACHMENT0)
                GL30.glDrawBuffers(attachments)
            }
        }

        if (GL32.glCheckFramebufferStatus(GL32.GL_FRAMEBUFFER) != GL32.GL_FRAMEBUFFER_COMPLETE) {
            throw RuntimeException("Frame buffer is not complete!")
        }

        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, 0)
    }

    fun bind(clear: Set<Int> = setOf(GL30.GL_COLOR_BUFFER_BIT, GL30.GL_DEPTH_BUFFER_BIT)) {
        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, fbo)
        GL32.glViewport(0, 0, resolution.width, resolution.height)
        if (hasDepthAttachment && clear.contains(GL30.GL_DEPTH_BUFFER_BIT))
            GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT)
        if (hasColorAttachment && clear.contains(GL11.GL_COLOR_BUFFER_BIT))
            GL30.glClear(GL11.GL_COLOR_BUFFER_BIT)
    }

    fun unbind() {
        val viewport = window.resolution
        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, 0)
        GL32.glViewport(0, 0, viewport.width, viewport.height)
    }

    fun dispose() {
        GL32.glDeleteFramebuffers(fbo)
        textures.values.forEach { it.dispose() }
        textures.clear()
    }

    fun resize(resolution: Resolution) {
        this.resolution = resolution
        dispose()
        create()
    }
}
