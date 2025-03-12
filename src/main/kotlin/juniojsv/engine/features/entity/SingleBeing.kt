package juniojsv.engine.features.entity

import juniojsv.engine.features.context.WindowContext
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.mesh.SphereMesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.shader.WhiteShaderProgram
import juniojsv.engine.features.utils.SphereBoundary
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
    private fun renderBoundary(context: WindowContext) = when (being.boundary) {
        is SphereBoundary -> {
            val transformation = Matrix4f(being.transformation())
            transformation.scale(being.boundary.radius)

            val mesh = SphereMesh.get()
            val shader = WhiteShaderProgram

            with(shader) {
                context.render.setShaderProgram(shader)
                putUniform("camera_projection", context.camera.projection)
                putUniform("camera_view", context.camera.view)
                putUniform("transformation", transformation)
            }

            context.render.setMesh(mesh)

            GL11.glDrawElements(
                GL11.GL_LINE_LOOP,
                mesh.getIndicesCount(),
                GL11.GL_UNSIGNED_INT, 0
            )
        }

        else -> {}
    }

    override fun render(context: WindowContext) {
        val transformation = being.transformation()
        val light = context.render.state.light
        val frustum = context.camera.frustum
        val camera = context.camera.instance

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
                context.render.setShaderProgram(this)
                putUniform("camera_projection", context.camera.projection)
                putUniform("camera_view", context.camera.view)
                putUniform("camera_position", camera.position)
                putUniform("transformation", transformation)
                putUniform("light_position", light?.position ?: Vector3f(0f))
                putUniform("light_color", light?.color ?: Vector3f(0f))
                putUniform("time", GLFW.glfwGetTime().toFloat())
                putUniform("texture_scale", being.textureScale)

                putUniform("has_texture", if (being.texture != null) 1 else 0)
                if (being.texture != null)
                    context.render.setTextures(setOf(being.texture))
                else context.render.setTextures(emptySet())
            }
        } else {
            context.render.setShaderProgram(null)
        }

        context.render.setMesh(mesh)

        GL11.glDrawElements(
            GL11.GL_TRIANGLES,
            mesh.getIndicesCount(),
            GL11.GL_UNSIGNED_INT, 0
        )
    }
}
