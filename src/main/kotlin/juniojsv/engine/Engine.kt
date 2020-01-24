package juniojsv.engine

import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import kotlin.math.cos
import kotlin.math.sin

class Engine : View("juniojsv.engine") {
    private var window: Long = -1
    private var polygonMode = GL11.GL_FILL
    private lateinit var beings: MutableList<Being>

    override fun setup(window: Long) {
        super.setup(window)
        this.window = window

        beings = mutableListOf(
            Being(
                Model.fromResources("porsche", "obj"),
                Shader.fromResources("default", "default"),
                scale = 0.30f
            ),
            Being(
                Model.fromResources("porsche", "obj"),
                Shader.fromResources("default", "uv"),
                scale = 0.30f
            )

        )
    }

    override fun draw() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClearColor(.25f, .25f, .25f, 1f)
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, polygonMode)

        beings.forEachIndexed { index, being ->
            with(being) {
                when(index) {
                    0 -> {
                        rotate(1f, 1f, 1f)
                        move(
                            -cos(GLFW.glfwGetTime()).toFloat() / 2,
                            sin(GLFW.glfwGetTime()).toFloat() / 2,
                            -2.0f
                        )
                    }
                    1 -> {
                        rotate(-1f, -1f, -1f)
                        move(
                            cos(GLFW.glfwGetTime()).toFloat() / 2,
                            -sin(GLFW.glfwGetTime()).toFloat() / 2,
                            -2.5f
                        )
                    }
                }
                draw(this@Engine)
            }
        }

        GLFW.glfwSwapBuffers(window)
        GLFW.glfwPollEvents()
    }

    override fun onKeyEvent(key: Int, code: Int, action: Int, mods: Int) {
        when (key) {
            GLFW.GLFW_KEY_ESCAPE ->
                GLFW.glfwSetWindowShouldClose(window, true)
            GLFW.GLFW_KEY_ENTER -> {
                if (action == GLFW.GLFW_PRESS)
                    polygonMode =
                        if (polygonMode == GL11.GL_FILL)
                            GL11.GL_LINE
                        else GL11.GL_FILL
            }
            else -> println("Key:$key Code:$code Action:$action")
        }
    }

    override fun onResize(width: Int, height: Int) {
        super.onResize(width, height)
        GL11.glViewport(0, 0, width, height)
    }
}