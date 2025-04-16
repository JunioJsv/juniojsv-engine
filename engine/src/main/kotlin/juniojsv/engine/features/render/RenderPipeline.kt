package juniojsv.engine.features.render

import juniojsv.engine.features.entity.BaseBeing
import juniojsv.engine.features.texture.CopyFrameBufferTexture
import juniojsv.engine.features.utils.FrameBuffer
import juniojsv.engine.features.utils.RenderPass
import juniojsv.engine.features.utils.RenderPassOverrides
import juniojsv.engine.features.utils.RenderTarget
import juniojsv.engine.features.utils.factories.QuadMesh
import juniojsv.engine.features.utils.factories.ShadersProgramFactory
import juniojsv.engine.features.window.Window
import org.lwjgl.opengl.GL30


class RenderPipeline(val window: Window) {

    interface ICallbacks {
        fun onRenderScene()
        fun onRenderDebugger()
    }

    val context get() = window.context

    private fun resolutionWithScale() = window.resolution.withResolutionScale(window.context.render.resolutionScale)
    private val resolution get() = window.resolution

    private val scene = RenderPass(FrameBuffer(window, resolutionWithScale()))
    private val overlay = RenderPass(FrameBuffer(window, resolutionWithScale()))
    private val velocity = RenderPass(
        FrameBuffer(
            window,
            resolutionWithScale(),
            colorInternalFormat = GL30.GL_RG16F
        ),
        RenderPassOverrides(
            shaders = mutableMapOf(
                RenderTarget.SINGLE to ShadersProgramFactory.create("VELOCITY"),
                RenderTarget.MULTI to ShadersProgramFactory.create("VELOCITY_INSTANCED")
            )
        )
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
            scene.fbo.resize(it)
            velocity.fbo.resize(it)
        }
        lastFrame.resize(resolution)
    }

    private fun onScenePass(callbacks: ICallbacks) {
        scene.bind(context)
        callbacks.onRenderScene()
        callbacks.onRenderDebugger()
        scene.unbind(context)
    }

    private fun onVelocityPass(callbacks: ICallbacks) {
        velocity.bind(context)
        callbacks.onRenderScene()
        velocity.unbind(context)
    }

    fun render(callbacks: ICallbacks) {
        onScenePass(callbacks)
        onVelocityPass(callbacks)

//        overlay.bind()
//        overlay.unbind()

        val fps = context.time.averageFps
        viewport.also {
            it.uniforms["uSceneTexture"] = scene.fbo.getColorTexture()
            it.uniforms["uSceneDepthTexture"] = scene.fbo.getDepthTexture()
            it.uniforms["uVelocityTexture"] = velocity.fbo.getColorTexture()
            it.uniforms["uOverlayTexture"] = overlay.fbo.getColorTexture()
            it.uniforms["uPreviousFrameTexture"] = lastFrame
            it.uniforms["uVelocityScale"] = ((fps / 180) * context.render.motionBlur).toFloat()
            it.render(context)
        }

        lastFrame.update()
    }
}