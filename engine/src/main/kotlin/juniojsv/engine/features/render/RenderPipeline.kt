package juniojsv.engine.features.render

import juniojsv.engine.features.entity.BaseBeing
import juniojsv.engine.features.texture.CopyFrameBufferTexture
import juniojsv.engine.features.utils.FrameBuffer
import juniojsv.engine.features.utils.RenderPass
import juniojsv.engine.features.utils.RenderPassOverrides
import juniojsv.engine.features.utils.RenderTarget
import juniojsv.engine.features.utils.factories.QuadMesh
import juniojsv.engine.features.utils.factories.ShadersProgramFactory
import juniojsv.engine.features.window.Resolution
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
    private val shadows = RenderPass(
        FrameBuffer(
            window,
            Resolution(1024 * 8, 1024 * 8),
            attachments = setOf(GL30.GL_DEPTH_ATTACHMENT)
        ),
        RenderPassOverrides(
            shaders = mutableMapOf(
                RenderTarget.SINGLE to ShadersProgramFactory.create("SHADOW_MAP"),
                RenderTarget.MULTI to ShadersProgramFactory.create("SHADOW_MAP_INSTANCED")
            ),
            isFrustumCullingEnabled = false
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
        scene.unbind(context)
    }

    private fun onVelocityPass(callbacks: ICallbacks) {
        velocity.bind(context)
        callbacks.onRenderScene()
        velocity.unbind(context)
    }

    private fun onShadowsPass(callbacks: ICallbacks) {
        val light = context.render.ambientLight ?: return
        val camera = context.camera.instance
        light.lerpPosition(camera.position, .995f)
        light.target = camera.position

        shadows.bind(context)
        shadows.overrides.uniforms["uLightSpaceMatrix"] = light.space
        GL30.glCullFace(GL30.GL_FRONT)
        callbacks.onRenderScene()
        GL30.glCullFace(GL30.GL_BACK)
        shadows.unbind(context)

        context.render.overrides.uniforms["uLightSpaceMatrix"] = light.space
        context.render.overrides.uniforms["uShadowMapTexture"] = shadows.fbo.getDepthTexture()
    }

    private fun onOverlayPass(callbacks: ICallbacks) {
        overlay.fbo.copy(scene.fbo, attachments = setOf(GL30.GL_DEPTH_ATTACHMENT))
        overlay.bind(context, clear = setOf(GL30.GL_COLOR_BUFFER_BIT))
        callbacks.onRenderDebugger()
        overlay.unbind(context)
    }

    fun render(callbacks: ICallbacks) {
        onShadowsPass(callbacks)
        onScenePass(callbacks)
        onVelocityPass(callbacks)
        onOverlayPass(callbacks)

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