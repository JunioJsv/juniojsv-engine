package juniojsv.engine

import juniojsv.engine.View.Companion.FAR
import juniojsv.engine.View.Companion.FOV
import juniojsv.engine.View.Companion.NEAR
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import kotlin.math.tan

class Being(
    private val model: Model,
    private val shader: Shader?,
    private val position: Vector3f = Vector3f(0f, 0f, -1f),
    private val rotation: Vector3f = Vector3f(0f),
    private var scale: Float = .5f
) {

    fun move(dx: Float, dy: Float, dz: Float, increment: Boolean = false) {
        position.apply {
            x = if (increment) x + dx else dx
            y = if (increment) y + dy else dy
            z = if (increment) z + dz else dz
        }
    }

    fun zoom(value: Float) {
        scale += value
    }

    fun rotate(rotX: Float, rotY: Float, rotZ: Float) {
        rotation.apply {
            x += rotX
            y += rotY
            z += rotZ
        }
    }

    fun draw(view: View) {
        val tMatrix: Matrix4f = Matrix4f()
            .apply {
                translate(position)
                rotate(Math.toRadians(rotation.x.toDouble()).toFloat(), 1f, 0f, 0f)
                rotate(Math.toRadians(rotation.y.toDouble()).toFloat(), 0f, 1f, 0f)
                rotate(Math.toRadians(rotation.z.toDouble()).toFloat(), 0f, 0f, 1f)
                scale(scale)
            }
        val pMatrix = Matrix4f().apply {
            val ratio = view.width.toFloat() / view.height.toFloat()
            val yScale =
                (1f / tan(Math.toRadians((FOV / 2f).toDouble())) * ratio).toFloat()
            val xScale = yScale / ratio
            val frustum: Float = FAR - NEAR

            m00(xScale)
            m11(yScale)
            m22(-((FAR + NEAR) / frustum))
            m23(-1f)
            m32(-(2 * NEAR * FAR / frustum))
            m33(0f)
        }

        shader?.apply {
            GL20.glUseProgram(this.identifier)
            putUniform("transformation", tMatrix)
            putUniform("projection", pMatrix)
            putUniform("sys_time", GLFW.glfwGetTime().toFloat())
        }

        GL30.glBindVertexArray(model.identifier)
        GL20.glEnableVertexAttribArray(0)

        GL11.glDrawElements(
            GL11.GL_TRIANGLES,
            model.instructionsCount,
            GL11.GL_UNSIGNED_INT, 0
        )

        GL20.glDisableVertexAttribArray(0)
        GL30.glBindVertexArray(0)
    }
}