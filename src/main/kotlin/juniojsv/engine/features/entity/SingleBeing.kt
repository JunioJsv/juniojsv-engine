package juniojsv.engine.features.entity

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.context.IWindowContextListener
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.texture.Texture
import juniojsv.engine.features.texture.Texture.Companion.bind
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11

class SingleBeing(
    private val mesh: Mesh,
    private val shader: ShadersProgram,
    private val being: BaseBeing = BaseBeing(),
    isDebuggable: Boolean = true,
    private val isFrustumCullingEnabled: Boolean = true,
    private val isPhysicsEnabled: Boolean = true,
    private val isShaderOverridable: Boolean = true
) : BeingRender(isDebuggable, isFrustumCullingEnabled, isPhysicsEnabled, isShaderOverridable),
    IWindowContextListener {
    private lateinit var context: IWindowContext

    override fun setup(context: IWindowContext) {
        super.setup(context)
        this.context = context
        context.addListener(this)
        val boundary = mesh.boundary
        if (isPhysicsEnabled && boundary != null) {
            being.createRigidBody(context, boundary)
        }
    }

    override fun render(context: IWindowContext) {
        super.render(context)
        val transformation: Matrix4f = being.transform.transformation()
        val light = context.render.ambientLight
        val frustum = context.camera.frustum
        val camera = context.camera.instance
        val boundary = mesh.boundary
        val shader = if (isShaderOverridable) Companion.shader ?: shader else shader

        if (isFrustumCullingEnabled && boundary != null) {
            val isInsideFrustum = boundary.isInsideFrustum(frustum, being.transform)
            if (!isInsideFrustum) return
            if (canDebug) context.render.debugBeings.add(boundary.getDebugBeing(being.transform))
        }
        shader.apply {
            bind()
            putUniform("uProjection", context.camera.projection)
            putUniform("uView", context.camera.view)
            putUniform("uPreviousProjection", context.camera.previousProjection)
            putUniform("uPreviousView", context.camera.previousView)
            putUniform("uCameraPosition", camera.position)
            putUniform("uModel", transformation)
            putUniform("uPreviousModel", being.transform.previous.transformation())
            putUniform("uLightPosition", light?.position ?: Vector3f(0f))
            putUniform("uLightColor", light?.color ?: Vector3f(0f))
            putUniform("uTime", context.time.elapsedInSeconds.toFloat())
            putUniform("uTextureScale", being.textureScale)

            being.texture?.bind() ?: emptySet<Texture>().bind()
            putUniforms(this)
        }
        mesh.bind()
        GL11.glDrawElements(
            GL11.GL_TRIANGLES, mesh.getIndicesCount(), GL11.GL_UNSIGNED_INT, 0
        )
    }

    override fun onPostRender(context: IWindowContext) {
        being.transform.setAsPrevious()
    }

    override fun dispose() {
        super.dispose()
        being.disposeRigidBody(context)
        context.removeListener(this)
    }

    companion object {
        var shader: ShadersProgram? = null
    }
}
