package juniojsv.engine.features.window

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.entity.BaseBeing
import juniojsv.engine.features.entity.SingleBeing
import juniojsv.engine.features.utils.FrameBuffer
import juniojsv.engine.features.utils.factories.QuadMesh
import juniojsv.engine.features.utils.factories.ShaderProgramFactory
import juniojsv.engine.features.utils.factories.ShaderPrograms

class WindowFrameBuffers(val window: Window) {

    fun resolution() = window.resolution.withResolutionScale(window.context.render.resolutionScale)

    private val windowFbo = FrameBuffer(
        window, resolution(), depth = true, color = true
    )

    private val windowBeing = SingleBeing(
        QuadMesh.create(),
        ShaderProgramFactory.create(ShaderPrograms.WINDOW),
        BaseBeing(texture = windowFbo.colorTexture),
        isFrustumCullingEnabled = false,
        isDebuggable = false,
        isPhysicsEnabled = false,
        isShaderOverridable = false
    )

    fun refresh() {
        val resolution = resolution()
        windowFbo.resize(resolution)
    }


    fun render(onRenderScene: (IWindowContext) -> Unit) {
        windowFbo.bind()
        onRenderScene(window.context)
        windowFbo.unbind()
        windowBeing.render(window.context)
    }
}