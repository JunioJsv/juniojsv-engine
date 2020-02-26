package juniojsv.engine

import juniojsv.engine.constants.DEFAULT_SHADER
import juniojsv.engine.constants.GROUND_TEXTURE
import juniojsv.engine.constants.SKYBOX_DEFAULT_CUBEMAP
import juniojsv.engine.constants.SKYBOX_SHADER
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import kotlin.system.exitProcess

class Engine : View("juniojsv.engine") {
    private var window: Long = -1
    private var mPolygon: Int = 0
    private lateinit var skyBox: SkyBox
    private lateinit var light: Light
    private lateinit var beings: MutableList<Being>
    private lateinit var camera: Camera
    override lateinit var gui: Gui

    override fun setup(window: Long) {
        super.setup(window)
        this.window = window
        mPolygon = GL11.GL_FILL
        skyBox = SkyBox(SKYBOX_DEFAULT_CUBEMAP, SKYBOX_SHADER)
        light = Light(Vector3f(0f, 35f, 0f))
        camera = Camera(this)
        gui = Gui(this)
        beings = mutableListOf(
            Being(
                Terrain.generate(128, 8f, 24f, 32f),
                GROUND_TEXTURE,
                DEFAULT_SHADER,
                position = Vector3f(-256f, -10f, 256f)
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

        Being.draw(
            camera,
            skyBox,
            light,
            beings as ArrayList<Being>
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
            GLFW.GLFW_KEY_W -> camera.move(CameraMovement.FORWARD, .25f)
            GLFW.GLFW_KEY_A -> camera.move(CameraMovement.LEFT, .25f)
            GLFW.GLFW_KEY_S -> camera.move(CameraMovement.BACKWARD, .25f)
            GLFW.GLFW_KEY_D -> camera.move(CameraMovement.RIGHT, .25f)
            GLFW.GLFW_KEY_Q -> camera.move(CameraMovement.UP, 1f)
            GLFW.GLFW_KEY_E -> camera.move(CameraMovement.DOWN, 1f)
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
}