package juniojsv.engine.features.utils

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.utils.RenderTarget.MULTI
import juniojsv.engine.features.utils.RenderTarget.SINGLE

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

class RenderPass(val fbo: FrameBuffer, val overrides: RenderPassOverrides = RenderPassOverrides.DEFAULT) {
    fun bind(context: IWindowContext) {
        fbo.bind()
        context.render.overrides = overrides
    }

    fun unbind(context: IWindowContext) {
        fbo.unbind()
        context.render.overrides = RenderPassOverrides.DEFAULT
    }
}