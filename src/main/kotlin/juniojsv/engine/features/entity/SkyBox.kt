package juniojsv.engine.features.entity

import juniojsv.engine.features.context.WindowContext
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.texture.CubeMapTexture
import juniojsv.engine.features.utils.Scale
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL30

class SkyBox(
    private val mesh: Mesh,
    val texture: CubeMapTexture,
    private val shader: ShadersProgram,
    var scale: Float = Scale.KILOMETER.length(1f)
) : IRender {
    private fun transformation(): Matrix4f = Matrix4f().scale(scale)

    override fun render(context: WindowContext) {
        val light = context.render.state.ambientLight
        GL30.glDisable(GL30.GL_DEPTH_TEST)

        with(shader) {
            context.render.setShaderProgram(shader)
            putUniform("light_color", light?.color ?: Vector3f(0f))
            putUniform("transformation", transformation())
            putUniform("camera_projection", context.camera.projection)
            putUniform("camera_view", context.camera.view)
            putUniform("time", GLFW.glfwGetTime().toFloat())
            context.render.setTextures(setOf(texture))
        }

        context.render.setMesh(mesh)

        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, mesh.getIndicesCount())
        GL30.glEnable(GL30.GL_DEPTH_TEST)
    }

}