package juniojsv.engine.features.utils

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.utils.RenderTarget.MULTI
import juniojsv.engine.features.utils.RenderTarget.SINGLE
import juniojsv.engine.platforms.constants.GL_COLOR_BUFFER_BIT
import juniojsv.engine.platforms.constants.GL_DEPTH_BUFFER_BIT

/**
 * [SINGLE] is used to render a single object.
 * [MULTI] is used to render instanced objects.
 */
enum class RenderTarget {
    SINGLE,
    MULTI
}

class RenderPassOverrides(
    val shaders: MutableMap<RenderTarget, ShadersProgram> = mutableMapOf(),
    val uniforms: MutableMap<String, Any> = mutableMapOf(),
    var isFrustumCullingEnabled: Boolean = true,
) {
    companion object {
        val DEFAULT = RenderPassOverrides()
    }
}

class RenderPass(
    val label: String,
    val fbo: FrameBuffer,
    val overrides: RenderPassOverrides = RenderPassOverrides.DEFAULT
) {
    fun bind(context: IWindowContext, clear: Set<Int> = setOf(GL_COLOR_BUFFER_BIT, GL_DEPTH_BUFFER_BIT)) {
        fbo.bind(clear)
        context.render.pass = this
    }

    fun unbind(context: IWindowContext) {
        fbo.unbind()
        context.render.pass = null
    }
}