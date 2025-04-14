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
import org.lwjgl.opengl.GL30

open class SingleBeing(
    mesh: Mesh,
    private val shader: ShadersProgram,
    private val being: BaseBeing = BaseBeing(),
    private val isDebuggable: Boolean = true,
    private val isFrustumCullingEnabled: Boolean = true,
    private val isPhysicsEnabled: Boolean = true,
    private val isShaderOverridable: Boolean = true,
    private val glMode: Int = GL30.GL_TRIANGLES,
    var isEnabled: Boolean = true,
) : BeingRender(mesh, isDebuggable), IWindowContextListener {
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

        if (isFrustumCullingEnabled && boundary != null) {
            val isInsideFrustum = being.isInsideFrustum(this)
            being.debugger?.being?.isEnabled = isInsideFrustum
            if (!isInsideFrustum) return
        }

        val transformation: Matrix4f = being.transform.transformation()
        val light = context.render.ambientLight
        val camera = context.camera.instance
        val shader = if (isShaderOverridable) Companion.shader ?: shader else shader

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
            applyUniforms(this)
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

    companion object {
        var shader: ShadersProgram? = null
    }
}
