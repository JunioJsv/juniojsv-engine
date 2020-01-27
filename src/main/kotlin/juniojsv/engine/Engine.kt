package juniojsv.engine

import juniojsv.engine.constants.BOX_MODEL
import juniojsv.engine.constants.DEFAULT_SHADER
import juniojsv.engine.constants.DRAGON_MODEL
import juniojsv.engine.constants.MONKEY_MODEL
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import kotlin.random.Random

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
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glCullFace(GL11.GL_BACK)
        GL11.glClearColor(.25f, .25f, .25f, 1f)

        beings = MutableList(50) { _ ->
            Being(
                (0..2).random().let { random ->
                    when (random) {
                        0 -> DRAGON_MODEL
                        1 -> BOX_MODEL
                        else -> MONKEY_MODEL
                    }
                },
                DEFAULT_SHADER,
                Vector3f(
                    (Random.nextFloat() - .5f) * 15,
                    (Random.nextFloat() - .5f) * 15,
                    -(Random.nextFloat() - .5f + 1) * 15
                ),
                Vector3f(
                    Random.nextFloat() * 360,
                    Random.nextFloat() * 360,
                    Random.nextFloat() * 360
                ),
                Random.nextFloat() * 0.45f
            )
        }

    }

    override fun draw() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)

        Being.draw(this, camera, beings as ArrayList<Being>)

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