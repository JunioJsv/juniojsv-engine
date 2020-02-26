package juniojsv.engine

import juniojsv.engine.constants.CUBEMAP_MODEL
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class Being(
    val model: Model,
    val texture: Texture?,
    val shader: Shader?,
    var position: Vector3f = Vector3f(0f),
    var rotation: Vector3f = Vector3f(0f),
    var scale: Float = .5f
) {

    fun move(offsetX: Float, offsetY: Float, offsetZ: Float, increment: Boolean = false) {
        position.apply {
            x = if (increment) x + offsetX else offsetX
            y = if (increment) y + offsetY else offsetY
            z = if (increment) z + offsetZ else offsetZ
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
            camera: Camera,
            skyBox: SkyBox,
            light: Light,
            beings: ArrayList<Being>
        ) {
            var previousModel: Int? = null
            var previousShader: Int? = null
            var previousTexture: Int? = null

            val cameraProjection = camera.projection()
            val cameraView = camera.view()
            val fogColor = Vector3f(1f)

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

                    if (shader != null) {
                        with(shader) {
                            if (previousShader != this.id) {
                                GL20.glUseProgram(this.id)
                                previousShader = this.id
                            }
                            putUniform("camera_projection", cameraProjection)
                            putUniform("camera_view", cameraView)
                            putUniform("camera_near", camera.near)
                            putUniform("camera_far", camera.far)
                            putUniform("transformation", transformation)
                            putUniform("light_position", light.position)
                            putUniform("light_color", light.color)
                            putUniform("fog_color", fogColor)
                            putUniform("sys_time", GLFW.glfwGetTime().toFloat())

                            if (texture != null) {
                                putUniform("has_texture", 1)
                                with(texture) {
                                    if (previousTexture != this.id) {
                                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.id)
                                        previousTexture = this.id
                                    }
                                }
                            } else {
                                putUniform("has_texture", 0)
                                GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
                                previousTexture = null
                            }

                        }
                    } else {
                        GL20.glUseProgram(0)
                        previousShader = null
                        previousTexture = null
                    }

                    if (previousModel != model.id) {
                        GL30.glBindVertexArray(model.id)
                        GL20.glEnableVertexAttribArray(0)
                        GL20.glEnableVertexAttribArray(1)
                        GL20.glEnableVertexAttribArray(2)
                        previousModel = model.id
                    }

                    GL11.glDrawElements(
                        GL11.GL_TRIANGLES,
                        model.indicesCount,
                        GL11.GL_UNSIGNED_INT, 0
                    )
                }
            }

            with(skyBox) {
                val transformation: Matrix4f = Matrix4f().scale(scale)
                GL11.glDepthMask(false)
                GL30.glBindVertexArray(CUBEMAP_MODEL.id)
                GL20.glEnableVertexAttribArray(0)

                with(shader) {
                    GL20.glUseProgram(this.id)
                    putUniform("transformation", transformation)
                    putUniform("camera_projection", cameraProjection)
                    putUniform("camera_view", cameraView)
                }

                GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, cubeMap.id)
                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, CUBEMAP_MODEL.indicesCount)
                GL11.glDepthMask(true)
            }
            GL30.glBindVertexArray(0)
        }
    }
}