package juniojsv.engine

import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import kotlin.math.tan

class Being(
    private val model: Model,
    private val texture: Texture?,
    private val shader: Shader?,
    val position: Vector3f = Vector3f(0f),
    val rotation: Vector3f = Vector3f(0f),
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

        fun draw(
            view: View,
            camera: Camera,
            light: Light,
            beings: ArrayList<Being>
        ) {
            var PREVIOUS_MODEL: Int? = null
            var PREVIOUS_SHADER: Int? = null
            var PREVIOUS_TEXTURE: Int? = null

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
                            (1f / tan(Math.toRadians((camera.fov / 2f).toDouble())) * ratio).toFloat()
                        val xScale = yScale / ratio
                        val frustum: Float = camera.far - camera.near

                        m00(xScale)
                        m11(yScale)
                        m22(-((camera.far + camera.near) / frustum))
                        m23(-1f)
                        m32(-(2 * camera.near * camera.far / frustum))
                        m33(0f)
                    }

                    if (shader != null) {
                        with(shader) {
                            if (PREVIOUS_SHADER != this.id) {
                                GL20.glUseProgram(this.id)
                                PREVIOUS_SHADER = this.id
                            }
                            putUniform("projection", projection)
                            putUniform("camera_view", camera.view())
                            putUniform("transformation", transformation)
                            putUniform("light_position", light.position)
                            putUniform("light_color", light.color)
                            putUniform("sys_time", GLFW.glfwGetTime().toFloat())

                            if (texture != null) {
                                putUniform("has_texture", 1)
                                with(texture) {
                                    if (PREVIOUS_TEXTURE != this.id) {
                                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.id)
                                        PREVIOUS_TEXTURE = this.id
                                    }
                                }
                            } else {
                                putUniform("has_texture", 0)
                                GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
                                PREVIOUS_TEXTURE = null
                            }

                        }
                    } else {
                        GL20.glUseProgram(0)
                        PREVIOUS_SHADER = null
                        PREVIOUS_TEXTURE = null
                    }

                    if (PREVIOUS_MODEL != model.id) {
                        GL30.glBindVertexArray(model.id)
                        GL20.glEnableVertexAttribArray(0)
                        GL20.glEnableVertexAttribArray(1)
                        GL20.glEnableVertexAttribArray(2)
                        PREVIOUS_MODEL = model.id
                    }

                    GL11.glDrawElements(
                        GL11.GL_TRIANGLES,
                        model.indicesCount,
                        GL11.GL_UNSIGNED_INT, 0
                    )
                }
            }
            GL20.glDisableVertexAttribArray(0)
            GL20.glDisableVertexAttribArray(1)
            GL20.glDisableVertexAttribArray(2)
            GL30.glBindVertexArray(0)
        }
    }
}