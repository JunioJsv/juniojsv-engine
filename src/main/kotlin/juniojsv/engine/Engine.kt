package juniojsv.engine

import juniojsv.engine.constants.*
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import kotlin.system.exitProcess

class Engine : View("juniojsv.engine") {
    private var window: Long = -1
    private var mPolygon: Int = 0
    private lateinit var skyBox: Entity.SkyBox
    private lateinit var light: Entity.Light
    private lateinit var beings: MutableList<Entity.Being>
    private lateinit var camera: Entity.Camera
    override lateinit var gui: Gui

    override fun setup(window: Long) {
        super.setup(window)
        this.window = window
        mPolygon = GL11.GL_FILL
        skyBox = Entity.SkyBox(SKYBOX_DEFAULT_CUBEMAP, SKYBOX_SHADER, 256f)
        light = Entity.Light(
            Vector3f(0f, 1f, 0f),
            Vector3f(1f)
        )
        camera = Entity.Camera(Vector3f(0f), this)
        gui = Gui(this)
        beings = mutableListOf(
            Entity.Being(
                Terrain.generate(128, 4f, 16f, 32f),
                GROUND_TEXTURE,
                DEFAULT_SHADER,
                Vector3f(-256f, -20f, 256f)
            )
        )
        Thread(gui, "juniojsv.debug").start()

        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glCullFace(GL11.GL_BACK)
        GL11.glClearColor(.25f, .25f, .25f, 1f)
    }

    override fun draw(delta: Double) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)

        Render.drawRow(
            camera, skyBox,
            light, beings.toTypedArray()
        )

        GLFW.glfwSwapBuffers(window)
        GLFW.glfwPollEvents()
    }

    override fun onCursorEvent(xPos: Double, yPos: Double) {
        camera.rotate(xPos, yPos)
    }

    override fun onMouseButtonEvent(button: Int, action: Int, mods: Int) {}

    override fun onKeyBoardEvent(key: Int, code: Int, action: Int, mods: Int) {
        when (key) {
            GLFW.GLFW_KEY_ESCAPE ->
                GLFW.glfwSetWindowShouldClose(window, true)
            GLFW.GLFW_KEY_F1 -> {
                if (action == GLFW.GLFW_PRESS) {
                    mPolygon =
                        if (mPolygon == GL11.GL_FILL)
                            GL11.GL_LINE
                        else GL11.GL_FILL
                    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, mPolygon)
                }
            }
            GLFW.GLFW_KEY_W -> camera.move(Entity.Camera.CameraMovement.FORWARD, 1.25f)
            GLFW.GLFW_KEY_A -> camera.move(Entity.Camera.CameraMovement.LEFT, 1.25f)
            GLFW.GLFW_KEY_S -> camera.move(Entity.Camera.CameraMovement.BACKWARD, 1.25f)
            GLFW.GLFW_KEY_D -> camera.move(Entity.Camera.CameraMovement.RIGHT, 1.25f)
            GLFW.GLFW_KEY_Q -> camera.move(Entity.Camera.CameraMovement.UP, 1f)
            GLFW.GLFW_KEY_E -> camera.move(Entity.Camera.CameraMovement.DOWN, 1f)
            else -> with(gui.console) {
                println(
                    debug(
                        "Engine channel debug",
                        "ENGINE",
                        "DEBUG",
                        "KEYBOARD",
                        "Key: $key Code: $code Action: $action"
                    )
                )
            }
        }
    }

    override fun onResize(width: Int, height: Int) {
        super.onResize(width, height)
        GL11.glViewport(0, 0, width, height)
    }

    override fun channel(method: String, args: List<String>?): Any? {
        GLFW.glfwMakeContextCurrent(window)
        when (method) {
            "exit" -> exitProcess(0)
            "get_beings" -> return beings
            "clear_beings" -> {
                beings.clear()
                with(gui.console) {
                    println(
                        debug(
                            "Engine channel debug",
                            "ENGINE",
                            "CHANNEL",
                            "NOTIFICATION",
                            "Beings cleaned"
                        )
                    )
                }
            }
            else ->
                gui.console.println(
                    """[ENGINE] Engine channel debug
                                Source: ENGINE
                                Type: CHANNEL
                                Severity: NOTIFICATION
                                Message: No methods named $method"""
                )
        }
        return null
    }

    private abstract class Render {
        companion object {
            private var PREVIOUS_MODEL: Int? = null
            private var PREVIOUS_TEXTURE: Int? = null
            private var PREVIOUS_SHADER: Int? = null

            fun drawRow(
                camera: Entity.Camera,
                skyBox: Entity.SkyBox,
                light: Entity.Light,
                beings: Array<Entity.Being>
            ) {
                val cameraProjection = camera.projection()
                val cameraView = camera.view()
                val fogColor = Vector3f(1f)

                beings.forEach { being ->
                    val transformation = being.transformation()

                    if (being.shader != null) {
                        with(being.shader) {
                            if (PREVIOUS_SHADER != id) {
                                GL20.glUseProgram(id)
                                PREVIOUS_SHADER = id
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

                            if (being.texture != null) {
                                putUniform("has_texture", 1)
                                with(being.texture) {
                                    if (PREVIOUS_TEXTURE != id) {
                                        GL11.glBindTexture(
                                            GL11.GL_TEXTURE_2D,
                                            being.texture.id
                                        )
                                        PREVIOUS_TEXTURE = id
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

                    if (PREVIOUS_MODEL != being.model.id) {
                        GL30.glBindVertexArray(being.model.id)
                        GL20.glEnableVertexAttribArray(0)
                        GL20.glEnableVertexAttribArray(1)
                        GL20.glEnableVertexAttribArray(2)
                        PREVIOUS_MODEL = being.model.id
                    }

                    GL11.glDrawElements(
                        GL11.GL_TRIANGLES,
                        being.model.indicesCount,
                        GL11.GL_UNSIGNED_INT, 0
                    )
                }

                with(skyBox) {
                    GL11.glDepthMask(false)
                    GL30.glBindVertexArray(CUBEMAP_MODEL.id)
                    GL20.glEnableVertexAttribArray(0)

                    with(shader) {
                        GL20.glUseProgram(id)
                        putUniform("transformation", transformation())
                        putUniform("camera_projection", cameraProjection)
                        putUniform("camera_view", cameraView)
                    }

                    GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, cubeMap.id)
                    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, CUBEMAP_MODEL.indicesCount)
                    GL11.glDepthMask(true)
                }
                GL30.glBindVertexArray(0)
                PREVIOUS_MODEL = null
                PREVIOUS_TEXTURE = null
                PREVIOUS_SHADER = null
            }
        }
    }
}