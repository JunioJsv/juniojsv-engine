package juniojsv.engine.features.entity

import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.texture.TwoDimensionTexture
import juniojsv.engine.features.window.IRenderContext
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11

class Being(
    private val mesh: Mesh,
    private val texture: TwoDimensionTexture?,
    private val shader: ShadersProgram?,
    private val position: Vector3f = Vector3f(0f),
    private val rotation: Vector3f = Vector3f(0f),
    var scale: Float = 1f
) : IRender {

    fun move(offsetX: Float, offsetY: Float, offsetZ: Float, increment: Boolean = false) {
        with(position) {
            x = if (increment) x + offsetX else offsetX
            y = if (increment) y + offsetY else offsetY
            z = if (increment) z + offsetZ else offsetZ
        }
    }

    fun rotate(offsetX: Float, offsetY: Float, offsetZ: Float, increment: Boolean = false) {
        with(rotation) {
            x += if (increment) x + offsetX else offsetX
            y += if (increment) y + offsetY else offsetY
            z += if (increment) z + offsetZ else offsetZ
        }
    }

    private fun transformation(): Matrix4f = Matrix4f()
        .apply {
            translate(position)
            rotate(Math.toRadians(rotation.x.toDouble()).toFloat(), 1f, 0f, 0f)
            rotate(Math.toRadians(rotation.y.toDouble()).toFloat(), 0f, 1f, 0f)
            rotate(Math.toRadians(rotation.z.toDouble()).toFloat(), 0f, 0f, 1f)
            scale(scale)
        }

    override fun render(context: IRenderContext, camera: Camera, light: Light) {
        val transformation = transformation()

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
                putUniform("fog_color", light.color)

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