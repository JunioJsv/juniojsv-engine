package juniojsv.engine.features.entity

import juniojsv.engine.Config
import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.texture.Texture
import juniojsv.engine.features.texture.Texture.Companion.bind
import org.joml.Vector3f
import org.lwjgl.opengl.GL11

data class SingleBeing(
    val mesh: Mesh,
    val shader: ShadersProgram,
    val being: BaseBeing = BaseBeing(),
    private val isDebuggable: Boolean = true,
    private val isFrustumCullingEnabled: Boolean = true
) : IRender {

    private val canDebug: Boolean
        get() = isDebuggable && Config.isDebug

    override fun render(context: IWindowContext) {
        val transformation = being.transformation()
        val light = context.render.ambientLight
        val frustum = context.camera.frustum
        val camera = context.camera.instance
        val boundary = mesh.boundary

        if (isFrustumCullingEnabled && boundary != null) {
            val position = Vector3f()
            transformation.transformPosition(Vector3f(0f), position)
            val isInsideFrustum = boundary.isInsideFrustum(frustum, position, being.scale)
            if (!isInsideFrustum) return
            if (canDebug)
                context.render.debugBeings.add(boundary.getDebugBeing(position, being.scale))
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
        being.texture?.bind() ?: emptyList<Texture>().bind()
        mesh.bind()
        GL11.glDrawElements(
            GL11.GL_TRIANGLES,
            mesh.getIndicesCount(),
            GL11.GL_UNSIGNED_INT, 0
        )
    }
}
