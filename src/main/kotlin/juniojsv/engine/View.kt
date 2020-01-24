package juniojsv.engine

import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL

abstract class View(
    title: String,
    var width: Int = 640,
    var height: Int = 480
) {
    init {
        if (!GLFW.glfwInit())
            throw Exception("Can't init GLFW")

        GLFW.glfwDefaultWindowHints()

        GLFW.glfwCreateWindow(
            width,
            height,
            title, 0, 0
        ).also { window ->
            GLFW.glfwSetKeyCallback(window) { _, key: Int, code: Int, action: Int, mods: Int ->
                onKeyEvent(key, code, action, mods)
            }
            GLFW.glfwSetWindowSizeCallback(window) { _, width: Int, height: Int ->
                onResize(width, height)
            }

            setup(window)
            while (!GLFW.glfwWindowShouldClose(window))
                this.draw()
        } ?: throw Exception("Can't create window")
    }

    open fun setup(window: Long) {
        GLFW.glfwMakeContextCurrent(window)
        GLFW.glfwShowWindow(window)
        GL.createCapabilities()
    }

    abstract fun draw()

    abstract fun onKeyEvent(key: Int, code: Int, action: Int, mods: Int)

    open fun onResize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    companion object {
        const val FOV = 70f
        const val NEAR = .1f
        const val FAR = 1000f
    }
}