package juniojsv.engine.features.window

import juniojsv.engine.features.entity.BaseBeing
import juniojsv.engine.features.entity.MultiBeing
import juniojsv.engine.features.entity.SingleBeing
import juniojsv.engine.features.entity.SkyBox
import juniojsv.engine.features.texture.CopyFrameBufferTexture
import juniojsv.engine.features.utils.FrameBuffer
import juniojsv.engine.features.utils.factories.QuadMesh
import juniojsv.engine.features.utils.factories.ShaderProgramFactory
import juniojsv.engine.features.utils.factories.ShaderPrograms
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
        ShaderProgramFactory.create(ShaderPrograms.WINDOW),
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

    private val velocityInstancedShader = ShaderProgramFactory.create(ShaderPrograms.VELOCITY_INSTANCED)
    private val velocityShader = ShaderProgramFactory.create(ShaderPrograms.VELOCITY)

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

        viewport.apply {
            uniforms["uSceneTexture"] = scene.getColorTexture()
            uniforms["uVelocityTexture"] = velocity.getColorTexture()
            uniforms["uOverlayTexture"] = overlay.getColorTexture()
            uniforms["uPreviousFrameTexture"] = lastFrame
            uniforms["uMotionBlur"] = context.render.motionBlur
            render(context)
        }
        lastFrame.update()
    }
}