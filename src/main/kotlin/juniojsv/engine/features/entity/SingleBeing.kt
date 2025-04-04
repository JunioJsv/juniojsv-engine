package juniojsv.engine.features.entity

import juniojsv.engine.Config
import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.texture.Texture
import juniojsv.engine.features.texture.Texture.Companion.bind
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11

data class SingleBeing(
    val mesh: Mesh,
    val shader: ShadersProgram,
    val being: BaseBeing = BaseBeing(),
    private val isDebuggable: Boolean = true,
    private val isFrustumCullingEnabled: Boolean = true,
    private val isPhysicsEnabled: Boolean = true
) : IRender {
    private var didSetup = false
    private val canDebug: Boolean
        get() = isDebuggable && Config.isDebug
    lateinit var context: IWindowContext

    private fun setup(context: IWindowContext) {
        didSetup = true
        this.context = context
        val boundary = mesh.boundary
        if (isPhysicsEnabled && boundary != null) {
            being.createRigidBody(context, boundary)
        }
    }

    override fun dispose() {
        super.dispose()
        being.disposeRigidBody(context)
    }

    override fun render(context: IWindowContext) {
        if (!didSetup) setup(context)

        val transformation: Matrix4f = being.transform.transformation()
        val light = context.render.ambientLight
        val frustum = context.camera.frustum
        val camera = context.camera.instance
        val boundary = mesh.boundary

        if (isFrustumCullingEnabled && boundary != null) {
            val isInsideFrustum = boundary.isInsideFrustum(frustum, being.transform)
            if (!isInsideFrustum) return
            if (canDebug) context.render.debugBeings.add(boundary.getDebugBeing(being.transform))
        }

        shader.apply {
            bind()
            putUniform("camera_projection", context.camera.projection)
            putUniform("camera_view", context.camera.view)
            putUniform("camera_position", camera.position)
            putUniform("transformation", transformation)
            putUniform("light_position", light?.position ?: Vector3f(0f))
            putUniform("light_color", light?.color ?: Vector3f(0f))
            putUniform("time", context.time.elapsedInSeconds.toFloat())
            putUniform("texture_scale", being.textureScale)
            putUniform("has_texture", if (being.texture != null) 1 else 0)
        }
        being.texture?.bind() ?: emptySet<Texture>().bind()
        mesh.bind()
        GL11.glDrawElements(
            GL11.GL_TRIANGLES, mesh.getIndicesCount(), GL11.GL_UNSIGNED_INT, 0
        )
    }
}
