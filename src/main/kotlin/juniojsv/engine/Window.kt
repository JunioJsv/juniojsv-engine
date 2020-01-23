package juniojsv.engine

import org.lwjgl.glfw.GLFW

abstract class Window {
    init {
        if (!GLFW.glfwInit())
            throw Exception("Can't init GLFW")

        GLFW.glfwDefaultWindowHints()

        GLFW.glfwCreateWindow(
            WIDTH,
            HEIGHT,
            "juniojsv.engine", 0, 0
        )?.also { window ->
            GLFW.glfwSetKeyCallback(window) { _, key: Int, code: Int, action: Int, mods: Int ->
                onKeyEvent(key, code, action, mods)
            }

            setup(window)

            while (!GLFW.glfwWindowShouldClose(window))
                this.draw()
        } ?: throw Exception("Can't create window")
    }

    abstract fun setup(window: Long)

    abstract fun draw()

    abstract fun onKeyEvent(key: Int, code: Int, action: Int, mods: Int)

    companion object {
        const val WIDTH = 640
        const val HEIGHT = 480
    }
}