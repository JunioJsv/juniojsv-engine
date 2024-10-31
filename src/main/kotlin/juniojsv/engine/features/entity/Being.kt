package juniojsv.engine.features.entity

import juniojsv.engine.DebugMode
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.mesh.SphereMesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.shader.WhiteShaderProgram
import juniojsv.engine.features.texture.TwoDimensionTexture
import juniojsv.engine.features.utils.BoundaryShape
import juniojsv.engine.features.utils.SphereBoundary
import juniojsv.engine.features.window.IRenderContext
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11

data class Being(
    private val mesh: Mesh,
    private val texture: TwoDimensionTexture?,
    private val shader: ShadersProgram?,
    val position: Vector3f = Vector3f(0f),
    val rotation: Vector3f = Vector3f(0f),
    val scale: Float = 1f,
    val boundary: BoundaryShape? = null
) : IRender {

    private fun transformation(): Matrix4f = Matrix4f()
        .apply {
            translate(position)
            rotate(Math.toRadians(rotation.x.toDouble()).toFloat(), 1f, 0f, 0f)
            rotate(Math.toRadians(rotation.y.toDouble()).toFloat(), 0f, 1f, 0f)
            rotate(Math.toRadians(rotation.z.toDouble()).toFloat(), 0f, 0f, 1f)
            scale(scale)
        }

    private fun renderBoundary(context: IRenderContext) = when (boundary) {
        is SphereBoundary -> {
            val transformation = Matrix4f(transformation())
            transformation.scale(boundary.radius)

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
        val transformation = transformation()
        val light = context.getAmbientLight()
        val frustum = context.getCameraFrustum()

        when (boundary) {
            is SphereBoundary -> {
                val transformedCenter = Vector3f()
                val effectiveRadius = boundary.radius * scale
                transformation.transformPosition(Vector3f(0f), transformedCenter)
                if (!frustum.isSphereInside(transformedCenter, effectiveRadius)) return
            }
        }

        if (shader != null) {
            with(shader) {
                context.setCurrentShaderProgram(this)
                putUniform("camera_projection", context.getCameraProjection())
                putUniform("camera_view", context.getCameraView())
                putUniform("transformation", transformation)
                putUniform("light_position", light?.position ?: Vector3f(0f))
                putUniform("light_color", light?.color ?: Vector3f(0f))
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
        if (DebugMode) renderBoundary(context)
    }
}
