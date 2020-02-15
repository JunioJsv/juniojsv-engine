package juniojsv.engine

import juniojsv.engine.constants.*
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import kotlin.math.cos
import kotlin.math.sin
import kotlin.system.exitProcess

class Engine : View("juniojsv.engine") {
    private var window: Long = -1
    private var mPolygon: Int = 0
    private lateinit var light: Light
    private lateinit var beings: MutableList<Being>
    private lateinit var camera: Camera
    override lateinit var gui: Gui

    override fun setup(window: Long) {
        super.setup(window)
        this.window = window
        mPolygon = GL11.GL_FILL
        light = Light(Vector3f(0f, 300f, 0f))
        camera = Camera(this)
        gui = Gui(this)
        beings = MutableList(1000) { index ->
            if (index == 0)
                Being(
                    model = SPHERE_MODEL,
                    texture = EARTH_TEXTURE,
                    shader = DEFAULT_SHADER
                )
            else
                Being(
                    model = ROCK_MODEL,
                    texture = ROCK_TEXTURE,
                    shader = DEFAULT_SHADER,
                    scale = 0.01f
                )
        }
        Thread(gui, "juniojsv.debug").start()

        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glCullFace(GL11.GL_BACK)
        GL11.glClearColor(.25f, .25f, .25f, 1f)
    }

    override fun draw(delta: Double) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)

        with(light) {
            val time = GLFW.glfwGetTime().toFloat()
            move(
                cos(time * 0.45f) * 25f,
                0f,
                -sin(time * 0.45f) * 25f
            )
        }

        beings.forEachIndexed { index, being ->
            when (index) {
                0 -> {
                }
                else -> {
                    val time = GLFW.glfwGetTime().toFloat()
                    val y = cos(time / 4 + index * sin(index.toFloat())) / 8
                    val x = cos(time + index)
                    val z = -sin(time + index)
                    being.move(x, y, z)
                }
            }
        }

        Being.draw(
            camera,
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