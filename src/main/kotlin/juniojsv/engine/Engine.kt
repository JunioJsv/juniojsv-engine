package juniojsv.engine

import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import kotlin.math.cos
import kotlin.math.sin

class Engine : View("juniojsv.engine") {
    private var window: Long = -1
    private var mPolygon: Int = 0
    private lateinit var beings: MutableList<Being>
    override lateinit var camera: Camera

    override fun setup(window: Long) {
        super.setup(window)
        this.window = window
        camera = Camera(this)
        mPolygon = GL11.GL_FILL
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glClearColor(.25f, .25f, .25f, 1f)

        GLFW.glfwSetCursorPos(
            window,
            (width / 2).toDouble(),
            (height / 2).toDouble()
        )

        beings = mutableListOf(
            Being(
                Model.fromResources("porsche", "obj"),
                Shader.fromResources("default", "default"),
                scale = 0.30f
            ),
            Being(
                Model.fromResources("dragon", "obj"),
                Shader.fromResources("default", "default"),
                scale = 0.10f
            )
        )
    }

    override fun draw() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)

        beings.forEachIndexed { index, being ->
            with(being) {
                when (index) {
                    0 -> {
                        rotate(1f, 1f, 1f)
                        move(
                            -cos(GLFW.glfwGetTime()).toFloat() / 2,
                            sin(GLFW.glfwGetTime()).toFloat() / 2,
                            -1.8f
                        )
                    }
                    1 -> {
                        rotate(0f, -1f, 0f)
                        move(
                            cos(GLFW.glfwGetTime()).toFloat() / 2,
                            -sin(GLFW.glfwGetTime()).toFloat() / 2,
                            -3f
                        )
                    }
                }
                draw(this@Engine, camera)
            }
        }

        GLFW.glfwSwapBuffers(window)
        GLFW.glfwPollEvents()
    }

    override fun onCursorEvent(xPos: Double, yPos: Double) {
        camera.rotate(xPos, yPos)
    }

    override fun onMouseButtonEvent(button: Int, action: Int, mods: Int) {
    }

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
            GLFW.GLFW_KEY_W -> camera.move(0f, 0f, -.1f, true)
            GLFW.GLFW_KEY_A -> camera.move(-.1f, 0f, 0f, true)
            GLFW.GLFW_KEY_S -> camera.move(0f, 0f, .1f, true)
            GLFW.GLFW_KEY_D -> camera.move(.1f, 0f, 0f, true)
            else -> println("Key:$key Code:$code Action:$action")
        }
    }

    override fun onResize(width: Int, height: Int) {
        super.onResize(width, height)
        GL11.glViewport(0, 0, width, height)
    }
}