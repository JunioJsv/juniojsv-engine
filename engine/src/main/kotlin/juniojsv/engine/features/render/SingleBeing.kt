package juniojsv.engine.features.render

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.context.IWindowContextListener
import juniojsv.engine.features.entity.BaseBeing
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.texture.Texture
import juniojsv.engine.features.texture.Texture.Companion.bind
import juniojsv.engine.features.utils.RenderTarget
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30

class SingleBeing(
    mesh: Mesh,
    shader: ShadersProgram,
    private val being: BaseBeing = BaseBeing(),
    isDebuggable: Boolean = true,
    isFrustumCullingEnabled: Boolean = true,
    private val isPhysicsEnabled: Boolean = true,
    isShaderOverridable: Boolean = true,
    private val glMode: Int = GL30.GL_TRIANGLES,
    isEnabled: Boolean = true,
) : BaseRender(
    RenderTarget.SINGLE,
    mesh,
    shader,
    isDebuggable,
    isShaderOverridable,
    isFrustumCullingEnabled,
    isEnabled
), IWindowContextListener {
    private val boundary = mesh.boundary

    override fun setup(context: IWindowContext) {
        super.setup(context)
        context.addListener(this)
        if (boundary != null) {
            if (isPhysicsEnabled) being.createRigidBody(this)
            if (isDebuggable) being.createDebugger(this)
        }
    }

    override fun render(context: IWindowContext) {
        super.render(context)

        if (!isEnabled || !being.isEnabled) return

        if (isFrustumCullingEnabled() && boundary != null) {
            val isInsideFrustum = being.isInsideFrustum(this)
            being.debugger?.being?.isEnabled = isInsideFrustum
            if (!isInsideFrustum) return
        }

        val transformation: Matrix4f = being.transform.transformation()
        val light = context.render.ambientLight
        val camera = context.camera.instance

        being.texture?.bind() ?: emptySet<Texture>().bind()

        getShader().also {
            it.bind()
            uniforms["uProjection"] = context.camera.projection
            uniforms["uView"] = context.camera.view
            uniforms["uPreviousProjection"] = context.camera.previousProjection
            uniforms["uPreviousView"] = context.camera.previousView
            uniforms["uCameraPosition"] = camera.position
            uniforms["uModel"] = transformation
            uniforms["uPreviousModel"] = being.transform.previous.transformation()
            uniforms["uLightPosition"] = light?.position ?: Vector3f(0f)
            uniforms["uLightColor"] = light?.color ?: Vector3f(0f)
            uniforms["uTime"] = context.time.elapsedInSeconds.toFloat()
            uniforms["uTextureScale"] = being.textureScale
            it.applyUniforms()
        }
        mesh.bind()
        GL11.glDrawElements(
            glMode, mesh.getIndicesCount(), GL11.GL_UNSIGNED_INT, 0
        )
    }

    override fun onPostRender(context: IWindowContext) {
        being.transform.setAsPrevious()
    }

    override fun dispose() {
        super.dispose()
        being.dispose(context)
        context.removeListener(this)
    }
}
