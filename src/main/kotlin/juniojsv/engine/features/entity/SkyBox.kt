package juniojsv.engine.features.entity

import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.texture.CubeMapTexture
import juniojsv.engine.features.window.IRenderContext
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class SkyBox(
    private val mesh: Mesh,
    val texture: CubeMapTexture,
    private val shader: ShadersProgram,
    var scale: Float = 500f
) : IRender {
    private fun transformation(): Matrix4f = Matrix4f().scale(scale)

    override fun render(context: IRenderContext, camera: Camera, light: Light) {
        GL30.glDisable(GL30.GL_DEPTH_TEST)
        context.setCurrentMesh(mesh)

        with(shader) {
            context.setCurrentShaderProgram(shader)
            putUniform("transformation", transformation())
            putUniform("camera_projection", camera.projection())
            putUniform("camera_view", camera.view())
        }

        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, texture.id)
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, mesh.getIndicesCount())
        GL30.glEnable(GL30.GL_DEPTH_TEST)
    }

}