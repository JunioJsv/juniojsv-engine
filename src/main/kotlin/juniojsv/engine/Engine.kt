package juniojsv.engine

import juniojsv.engine.entities.Being
import juniojsv.engine.entities.Camera
import juniojsv.engine.entities.Light
import juniojsv.engine.entities.SkyBox
import juniojsv.engine.essentials.Shell
import juniojsv.engine.utils.Effects
import juniojsv.engine.utils.Shells
import juniojsv.engine.utils.Textures
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Engine : View("juniojsv.engine") {
    private lateinit var skyBox: SkyBox
    private lateinit var light: Light
    private lateinit var beings: MutableList<Being>
    private lateinit var camera: Camera

    override fun setup() {
        super.setup()
        skyBox = SkyBox(
                Shells.CUBEMAP_SHELL,
                Textures.SKYBOX_DEFAULT_CUBEMAP,
                Effects.SKYBOX_EFFECT, 256f
        )
        light = Light(
                Vector3f(0f, 1f, 0f),
                Vector3f(1f)
        )
        camera = Camera(Vector3f(0f), this)
        beings = mutableListOf(
                Being(
                        Shell.Terrain(128, 4f, 16f, 32f),
                        Textures.GROUND_TEXTURE,
                        Effects.DEFAULT_EFFECT,
                        Vector3f(-256f, -20f, 256f)
                )
        )

        repeat(128) {
            beings.add(
                    Being(
                            Shells.SPHERE_SHELL,
                            Textures.UV_TEXTURE,
                            Effects.DEFAULT_EFFECT,
                            Vector3f(
                                    Random.nextFloat() * 500f - 250f,
                                    Random.nextFloat() * 256f,
                                    Random.nextFloat() * 500f - 250f
                            ),
                            scale = Random.nextFloat() * 5f
                    )
            )
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glCullFace(GL11.GL_BACK)
        GL11.glClearColor(.25f, .25f, .25f, 1f)
    }

    override fun draw() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)

        Render.frame(
                camera, skyBox,
                light, beings.toTypedArray()
        )

        GLFW.glfwSwapBuffers(window)
        GLFW.glfwPollEvents()
    }

    override fun onCursorEvent(positionX: Double, positionY: Double) {
        camera.rotate(positionX, positionY)
    }

    override fun onMouseButtonEvent(button: Int, action: Int, mods: Int) {}

    override fun onKeyBoardEvent(key: Int, code: Int, action: Int, mods: Int) {
        when (key) {
            GLFW.GLFW_KEY_ESCAPE ->
                GLFW.glfwSetWindowShouldClose(window, true)
            GLFW.GLFW_KEY_W -> camera.move(Camera.CameraMovement.FORWARD, .65f)
            GLFW.GLFW_KEY_A -> camera.move(Camera.CameraMovement.LEFT, .65f)
            GLFW.GLFW_KEY_S -> camera.move(Camera.CameraMovement.BACKWARD, 1f)
            GLFW.GLFW_KEY_D -> camera.move(Camera.CameraMovement.RIGHT, .65f)
            GLFW.GLFW_KEY_Q -> camera.move(Camera.CameraMovement.UP, 1f)
            GLFW.GLFW_KEY_E -> camera.move(Camera.CameraMovement.DOWN, 1f)
        }
    }

    override fun onResize(width: Int, height: Int) {
        super.onResize(width, height)
        GL11.glViewport(0, 0, width, height)
    }

    private object Render {
        fun frame(
                camera: Camera,
                skyBox: SkyBox,
                light: Light,
                beings: Array<Being>
        ) {
            var previousShell: Int? = null
            var previousTexture: Int? = null
            var previousShader: Int? = null

            val cameraProjection = camera.projection()
            val cameraView = camera.view()
            val fogColor = Vector3f(0f)

            val sysTime = GLFW.glfwGetTime().toFloat()
            val offset = Vector3f(sin(sysTime), cos(-sysTime), sin(sysTime + sysTime))

            beings.forEach { being ->
                val transformation = being.transformation()

                if (being.effect != null) {
                    with(being.effect) {
                        if (previousShader != id) {
                            GL20.glUseProgram(id)
                            previousShader = id
                        }
                        putUniform("camera_projection", cameraProjection)
                        putUniform("camera_view", cameraView)
                        putUniform("camera_near", camera.near)
                        putUniform("camera_far", camera.far)
                        putUniform("transformation", transformation)
                        putUniform("light_position", light.position)
                        putUniform("light_color", light.color)
                        putUniform("fog_color", fogColor)
                        putUniform("offset", offset)

                        if (being.texture != null) {
                            putUniform("has_texture", 1)
                            with(being.texture) {
                                if (previousTexture != id) {
                                    GL11.glBindTexture(
                                            GL11.GL_TEXTURE_2D,
                                            being.texture.id
                                    )
                                    previousTexture = id
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

                if (previousShell != being.shell.id) {
                    GL30.glBindVertexArray(being.shell.id)
                    GL20.glEnableVertexAttribArray(0)
                    GL20.glEnableVertexAttribArray(1)
                    GL20.glEnableVertexAttribArray(2)
                    previousShell = being.shell.id
                }

                GL11.glDrawElements(
                        GL11.GL_TRIANGLES,
                        being.shell.indicesCount,
                        GL11.GL_UNSIGNED_INT, 0
                )
            }

            with(skyBox) {
                GL11.glDepthMask(false)
                GL30.glBindVertexArray(shell.id)
                GL20.glEnableVertexAttribArray(0)

                with(effect) {
                    GL20.glUseProgram(id)
                    putUniform("transformation", transformation())
                    putUniform("camera_projection", cameraProjection)
                    putUniform("camera_view", cameraView)
                    putUniform("offset", offset)
                }

                GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, texture.id)
                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, shell.indicesCount)
                GL11.glDepthMask(true)
            }
            GL30.glBindVertexArray(0)
        }
    }
}