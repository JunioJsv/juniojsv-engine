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
    val position: Vector3f = Vector3f(0f, 0f, -1f),
    private val rotation: Vector3f = Vector3f(0f),
    var scale: Float = .5f
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

    companion object {


        fun draw(view: View, camera: Camera, beings: ArrayList<Being>) {
            var PREVIOUS_MODEL: Int? = null
            var PREVIOUS_SHADER: Int? = null

            beings.forEach { being ->
                with(being) {
                    val transformation: Matrix4f = Matrix4f()
                        .apply {
                            translate(position)
                            rotate(Math.toRadians(rotation.x.toDouble()).toFloat(), 1f, 0f, 0f)
                            rotate(Math.toRadians(rotation.y.toDouble()).toFloat(), 0f, 1f, 0f)
                            rotate(Math.toRadians(rotation.z.toDouble()).toFloat(), 0f, 0f, 1f)
                            scale(scale)
                        }
                    val projection = Matrix4f().apply {
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
                        if(PREVIOUS_SHADER != this.identifier) {
                            GL20.glUseProgram(this.identifier)
                            PREVIOUS_SHADER = this.identifier
                        }
                        putUniform("projection", projection)
                        putUniform("camera", camera.view())
                        putUniform("transformation", transformation)
                        putUniform("sys_time", GLFW.glfwGetTime().toFloat())
                    }

                    if(PREVIOUS_MODEL != model.identifier) {
                        GL30.glBindVertexArray(model.identifier)
                        GL20.glEnableVertexAttribArray(0)
                        GL20.glEnableVertexAttribArray(2)
                        PREVIOUS_MODEL = model.identifier
                    }

                    GL11.glDrawElements(
                        GL11.GL_TRIANGLES,
                        model.instructionsCount,
                        GL11.GL_UNSIGNED_INT, 0
                    )
                }
            }
            GL20.glDisableVertexAttribArray(0)
            GL20.glDisableVertexAttribArray(2)
            GL30.glBindVertexArray(0)
        }
    }
}