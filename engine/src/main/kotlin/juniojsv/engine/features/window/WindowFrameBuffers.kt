package juniojsv.engine.features.window

import juniojsv.engine.features.entity.BaseBeing
import juniojsv.engine.features.entity.MultiBeing
import juniojsv.engine.features.entity.SingleBeing
import juniojsv.engine.features.entity.SkyBox
import juniojsv.engine.features.texture.CopyFrameBufferTexture
import juniojsv.engine.features.utils.FrameBuffer
import juniojsv.engine.features.utils.factories.QuadMesh
import juniojsv.engine.features.utils.factories.ShadersProgramFactory
import org.lwjgl.opengl.GL30


class WindowFrameBuffers(val window: Window) {

    interface IRenderCallbacks {
        fun onRenderScene()
        fun onRenderOverlay()
    }

    val context get() = window.context

    private fun resolutionWithScale() = window.resolution.withResolutionScale(window.context.render.resolutionScale)
    private val resolution get() = window.resolution

    private val scene = FrameBuffer(window, resolutionWithScale())
    private val overlay = FrameBuffer(window, resolutionWithScale())
    private val velocity = FrameBuffer(
        window,
        resolutionWithScale(),
        colorInternalFormat = GL30.GL_RG16F
    )

    private val viewport = SingleBeing(
        QuadMesh.create(),
        ShadersProgramFactory.create("WINDOW"),
        BaseBeing(),
        isFrustumCullingEnabled = false,
        isDebuggable = false,
        isPhysicsEnabled = false,
        isShaderOverridable = false
    )

    private val lastFrame = CopyFrameBufferTexture(resolution)

    fun refresh() {
        resolutionWithScale().also {
            scene.resize(it)
            velocity.resize(it)
        }
        lastFrame.resize(resolution)
    }

    private val velocityInstancedShader = ShadersProgramFactory.create("VELOCITY_INSTANCED")
    private val velocityShader = ShadersProgramFactory.create("VELOCITY")

    fun render(callbacks: IRenderCallbacks) {
        scene.bind()
        callbacks.onRenderScene()
        callbacks.onRenderOverlay()
        scene.unbind()

        velocity.bind()
        MultiBeing.shader = velocityInstancedShader
        SingleBeing.shader = velocityShader
        SkyBox.shader = velocityShader
        callbacks.onRenderScene()
        MultiBeing.shader = null
        SingleBeing.shader = null
        SkyBox.shader = null
        velocity.unbind()

//        overlay.bind()
//        overlay.unbind()

        viewport.uniforms["uSceneTexture"] = scene.getColorTexture()
        viewport.uniforms["uVelocityTexture"] = velocity.getColorTexture()
        viewport.uniforms["uOverlayTexture"] = overlay.getColorTexture()
        viewport.uniforms["uPreviousFrameTexture"] = lastFrame
        viewport.uniforms["uMotionBlur"] = context.render.motionBlur
        viewport.render(context)

        lastFrame.update()
    }
}