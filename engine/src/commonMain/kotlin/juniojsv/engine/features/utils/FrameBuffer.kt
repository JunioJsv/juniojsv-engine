package juniojsv.engine.features.utils

import juniojsv.engine.features.textures.ColorTexture
import juniojsv.engine.features.textures.DepthTexture
import juniojsv.engine.features.textures.Texture
import juniojsv.engine.features.window.PlatformWindow
import juniojsv.engine.features.window.Resolution
import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.constants.GL_COLOR_ATTACHMENT0
import juniojsv.engine.platforms.constants.GL_COLOR_BUFFER_BIT
import juniojsv.engine.platforms.constants.GL_DEPTH_ATTACHMENT
import juniojsv.engine.platforms.constants.GL_DEPTH_BUFFER_BIT
import juniojsv.engine.platforms.constants.GL_DRAW_FRAMEBUFFER
import juniojsv.engine.platforms.constants.GL_FRAMEBUFFER
import juniojsv.engine.platforms.constants.GL_FRAMEBUFFER_COMPLETE
import juniojsv.engine.platforms.constants.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT
import juniojsv.engine.platforms.constants.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER
import juniojsv.engine.platforms.constants.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT
import juniojsv.engine.platforms.constants.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER
import juniojsv.engine.platforms.constants.GL_FRAMEBUFFER_UNSUPPORTED
import juniojsv.engine.platforms.constants.GL_LINEAR
import juniojsv.engine.platforms.constants.GL_NEAREST
import juniojsv.engine.platforms.constants.GL_NONE
import juniojsv.engine.platforms.constants.GL_READ_FRAMEBUFFER
import juniojsv.engine.platforms.constants.GL_RGBA
import juniojsv.engine.platforms.constants.GL_TEXTURE_2D
import kotlin.properties.Delegates

class FrameBuffer(
    private val window: PlatformWindow,
    private var resolution: Resolution,
    attachments: Set<Int> = setOf(GL_COLOR_ATTACHMENT0, GL_DEPTH_ATTACHMENT),
    private val colorInternalFormat: Int = GL_RGBA
) {
    private var fbo by Delegates.notNull<Int>()

    private val hasColorAttachment = attachments.contains(GL_COLOR_ATTACHMENT0)
    private val hasDepthAttachment = attachments.contains(GL_DEPTH_ATTACHMENT)

    private val textures = mutableMapOf<Int, Texture>()

    init {
        create()
    }

    fun getDepthTexture(): DepthTexture {
        return textures.getOrPut(GL_DEPTH_ATTACHMENT) {
            DepthTexture(resolution.width, resolution.height)
        } as DepthTexture
    }

    fun getColorTexture(): ColorTexture {
        return textures.getOrPut(GL_COLOR_ATTACHMENT0) {
            ColorTexture(
                resolution.width,
                resolution.height,
                internalFormat = colorInternalFormat
            )
        } as ColorTexture
    }

    fun copy(
        source: FrameBuffer,
        attachments: Set<Int> = setOf(GL_COLOR_ATTACHMENT0, GL_DEPTH_ATTACHMENT)
    ) {
        GL.glBindFramebuffer(GL_READ_FRAMEBUFFER, source.fbo)
        GL.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo)
        if (attachments.contains(GL_COLOR_ATTACHMENT0) && source.hasColorAttachment)
            GL.glBlitFramebuffer(
                0, 0, source.resolution.width, source.resolution.height,
                0, 0, resolution.width, resolution.height,
                GL_COLOR_BUFFER_BIT, GL_LINEAR
            )
        if (attachments.contains(GL_DEPTH_ATTACHMENT) && source.hasDepthAttachment)
            GL.glBlitFramebuffer(
                0, 0, source.resolution.width, source.resolution.height,
                0, 0, resolution.width, resolution.height,
                GL_DEPTH_BUFFER_BIT, GL_NEAREST
            )
        GL.glBindFramebuffer(GL_READ_FRAMEBUFFER, 0)
        GL.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0)
    }

    private fun create() {
        fbo = GL.glGenFramebuffers()
        GL.glBindFramebuffer(GL_FRAMEBUFFER, fbo)

        if (hasDepthAttachment) {
            val texture = getDepthTexture()
            GL.glFramebufferTexture2D(
                GL_FRAMEBUFFER,
                GL_DEPTH_ATTACHMENT,
                GL_TEXTURE_2D,
                texture.id,
                0
            )

            if (!hasColorAttachment) {
                GL.glDrawBuffers(GL_NONE)
                GL.glReadBuffer(GL_NONE)
            }
        }

        if (hasColorAttachment) {
            val texture = getColorTexture()
            GL.glFramebufferTexture2D(
                GL_FRAMEBUFFER,
                GL_COLOR_ATTACHMENT0,
                GL_TEXTURE_2D,
                texture.id,
                0
            )

            if (!hasDepthAttachment) {
                val attachments = intArrayOf(GL_COLOR_ATTACHMENT0)
                GL.glDrawBuffers(attachments)
            }
        }

        val status = GL.glCheckFramebufferStatus(GL_FRAMEBUFFER)
        if (status != GL_FRAMEBUFFER_COMPLETE) {
            val message = when (status) {
                GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> "incomplete attachment"
                GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT -> "missing attachment"
                GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER -> "incomplete draw buffer"
                GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER -> "incomplete read buffer"
                GL_FRAMEBUFFER_UNSUPPORTED -> "unsupported combination of formats"
                else -> "unknown error"
            }
            throw IllegalStateException("Frame buffer is not complete ($message)")
        }

        GL.glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    fun bind(clear: Set<Int> = setOf(GL_COLOR_BUFFER_BIT, GL_DEPTH_BUFFER_BIT)) {
        GL.glBindFramebuffer(GL_FRAMEBUFFER, fbo)
        GL.glViewport(0, 0, resolution.width, resolution.height)
        if (hasDepthAttachment && clear.contains(GL_DEPTH_BUFFER_BIT))
            GL.glClear(GL_DEPTH_BUFFER_BIT)
        if (hasColorAttachment && clear.contains(GL_COLOR_BUFFER_BIT))
            GL.glClear(GL_COLOR_BUFFER_BIT)
    }

    fun unbind() {
        val viewport = window.resolution
        GL.glBindFramebuffer(GL_FRAMEBUFFER, 0)
        GL.glViewport(0, 0, viewport.width, viewport.height)
    }

    fun dispose() {
        GL.glDeleteFramebuffers(fbo)
        textures.values.forEach { it.dispose() }
        textures.clear()
    }

    fun resize(resolution: Resolution) {
        if (this.resolution == resolution) return
        this.resolution = resolution
        dispose()
        create()
    }
}