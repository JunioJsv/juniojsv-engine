package juniojsv.engine

import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class Engine : Window() {
    private var window: Long = -1

    override fun setup(window: Long) {
        this.window = window
        GLFW.glfwMakeContextCurrent(this.window)
        GLFW.glfwShowWindow(this.window)
        GL.createCapabilities()
    }

    override fun draw() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)

        val model = Model.fromRaw(
            floatArrayOf(
                -.5f, .5f, 0f,
                -.5f, -.75f, 0f,
                .5f, .5f, 0f,
                .5f, -.75f, 0f,
                0f, .75f, 0f
            ),
            intArrayOf(
                0, 1, 2,
                3, 1, 2,
                0, 2, 4
            )
        )

        GL30.glBindVertexArray(model.identifier)
        GL20.glEnableVertexAttribArray(0)

        GL11.glDrawElements(
            GL11.GL_TRIANGLES,
            model.instructionsCount,
            GL11.GL_UNSIGNED_INT, 0
        )

        GL20.glDisableVertexAttribArray(0)
        GL30.glBindVertexArray(0)
        GL30.glDeleteVertexArrays(model.identifier)

        GLFW.glfwSwapBuffers(window)
        GLFW.glfwPollEvents()
    }

    override fun onKeyEvent(key: Int, code: Int, action: Int, mods: Int) {
        when (key) {
            GLFW.GLFW_KEY_ESCAPE ->
                GLFW.glfwSetWindowShouldClose(window, true)
            else -> println("Key:$key Code:$code Action:$action")
        }
    }
}