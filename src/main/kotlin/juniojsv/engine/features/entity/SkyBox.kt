package juniojsv.engine.features.entity

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.texture.FileCubeMapTexture
import juniojsv.engine.features.utils.Scale
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL30

class SkyBox(
    private val mesh: Mesh,
    val texture: FileCubeMapTexture,
    private val shader: ShadersProgram,
    var scale: Float = Scale.KILOMETER.length(1f),
    private val isShaderOverridable: Boolean = true
) : IRender {
    private fun transformation(): Matrix4f = Matrix4f().scale(scale)

    override fun render(context: IWindowContext) {
        val light = context.render.ambientLight
        GL30.glDisable(GL30.GL_DEPTH_TEST)

        val shader = if (isShaderOverridable) Companion.shader ?: shader else shader

        shader.apply {
            bind()
            putUniform("light_color", light?.color ?: Vector3f(0f))
            putUniform("transformation", transformation())
            putUniform("camera_projection", context.camera.projection)
            putUniform("camera_view", context.camera.view)
            putUniform("previous_camera_projection", context.camera.previousProjection)
            putUniform("previous_camera_view", context.camera.previousView)
            putUniform("time", context.time.elapsedInSeconds.toFloat())
        }
        texture.bind()
        mesh.bind()
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, mesh.getIndicesCount())

        GL30.glEnable(GL30.GL_DEPTH_TEST)
    }

    companion object {
        var shader: ShadersProgram? = null
    }
}