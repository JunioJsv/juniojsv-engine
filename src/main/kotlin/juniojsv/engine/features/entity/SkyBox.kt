package juniojsv.engine.features.entity

import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.texture.CubeMapTexture
import juniojsv.engine.features.utils.Scale
import juniojsv.engine.features.window.IRenderContext
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

    override fun render(context: IRenderContext) {
        val light = context.getAmbientLight()
        GL30.glDisable(GL30.GL_DEPTH_TEST)
        context.setCurrentMesh(mesh)

        with(shader) {
            context.setCurrentShaderProgram(shader)
            putUniform("light_color", light?.color ?: Vector3f(0f))
            putUniform("transformation", transformation())
            putUniform("camera_projection", context.getCameraProjection())
            putUniform("camera_view", context.getCameraView())
            putUniform("time", GLFW.glfwGetTime().toFloat())
        }

        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, texture.id)
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, mesh.getIndicesCount())
        GL30.glEnable(GL30.GL_DEPTH_TEST)
    }

}