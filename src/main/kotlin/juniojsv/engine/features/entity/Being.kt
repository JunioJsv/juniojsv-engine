package juniojsv.engine.features.entity

import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.texture.TwoDimensionTexture
import juniojsv.engine.features.window.IRenderContext
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11

class Being(
    private val mesh: Mesh,
    private val texture: TwoDimensionTexture?,
    private val shader: ShadersProgram?,
    @Suppress("UNUSED_PARAMETER")
    val position: Vector3f = Vector3f(0f),
    @Suppress("UNUSED_PARAMETER")
    val rotation: Vector3f = Vector3f(0f),
    var scale: Float = 1f
) : IRender {

    private fun transformation(): Matrix4f = Matrix4f()
        .apply {
            translate(position)
            rotate(Math.toRadians(rotation.x.toDouble()).toFloat(), 1f, 0f, 0f)
            rotate(Math.toRadians(rotation.y.toDouble()).toFloat(), 0f, 1f, 0f)
            rotate(Math.toRadians(rotation.z.toDouble()).toFloat(), 0f, 0f, 1f)
            scale(scale)
        }

    override fun render(context: IRenderContext) {
        val transformation = transformation()
        val camera = context.getCamera()
        val light = context.getAmbientLight()!!

        if (shader != null) {
            with(shader) {
                context.setCurrentShaderProgram(this)
                putUniform("camera_projection", camera.projection())
                putUniform("camera_view", camera.view())
                putUniform("camera_near", camera.near)
                putUniform("camera_far", camera.far)
                putUniform("transformation", transformation)
                putUniform("light_position", light.position)
                putUniform("light_color", light.color)
                putUniform("time", GLFW.glfwGetTime().toFloat())

                putUniform("has_texture", if (texture != null) 1 else 0)
                context.setCurrentTexture(texture)
            }
        } else {
            context.setCurrentShaderProgram(null)
        }

        context.setCurrentMesh(mesh)

        GL11.glDrawElements(
            GL11.GL_TRIANGLES,
            mesh.getIndicesCount(),
            GL11.GL_UNSIGNED_INT, 0
        )
    }
}