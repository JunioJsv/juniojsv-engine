package juniojsv.engine.features.entity

import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.mesh.SphereMesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.shader.WhiteShaderProgram
import juniojsv.engine.features.utils.SphereBoundary
import juniojsv.engine.features.window.IRenderContext
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11

data class SingleBeing(
    val mesh: Mesh,
    val shader: ShadersProgram?,
    val being: BaseBeing
) : IRender {

    @Deprecated("")
    private fun renderBoundary(context: IRenderContext) = when (being.boundary) {
        is SphereBoundary -> {
            val transformation = Matrix4f(being.transformation())
            transformation.scale(being.boundary.radius)

            val mesh = SphereMesh.get()
            val shader = WhiteShaderProgram

            with(shader) {
                context.setCurrentShaderProgram(shader)
                putUniform("camera_projection", context.getCameraProjection())
                putUniform("camera_view", context.getCameraView())
                putUniform("transformation", transformation)
            }

            context.setCurrentMesh(mesh)

            GL11.glDrawElements(
                GL11.GL_LINE_LOOP,
                mesh.getIndicesCount(),
                GL11.GL_UNSIGNED_INT, 0
            )
        }

        else -> {}
    }

    override fun render(context: IRenderContext) {
        val transformation = being.transformation()
        val light = context.getAmbientLight()
        val frustum = context.getCameraFrustum()
        val camera = context.getCamera()

        when (being.boundary) {
            is SphereBoundary -> {
                val transformedCenter = Vector3f()
                val effectiveRadius = being.boundary.radius * being.scale
                transformation.transformPosition(Vector3f(0f), transformedCenter)
                if (!frustum.isSphereInside(transformedCenter, effectiveRadius)) return
            }
        }

        if (shader != null) {
            with(shader) {
                context.setCurrentShaderProgram(this)
                putUniform("camera_projection", context.getCameraProjection())
                putUniform("camera_view", context.getCameraView())
                putUniform("camera_position", camera.position)
                putUniform("transformation", transformation)
                putUniform("light_position", light?.position ?: Vector3f(0f))
                putUniform("light_color", light?.color ?: Vector3f(0f))
                putUniform("time", GLFW.glfwGetTime().toFloat())

                putUniform("has_texture", if (being.texture != null) 1 else 0)
                context.setCurrentTexture(being.texture)
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
