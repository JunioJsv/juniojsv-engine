package juniojsv.engine

import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GLUtil
import java.io.OutputStream
import java.io.PrintStream

abstract class View(
    title: String,
    var width: Int = 640,
    var height: Int = 480
) {
    abstract var camera: Camera

    init {
        if (!GLFW.glfwInit())
            throw Exception("Can't init GLFW")

        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE)

        GLFW.glfwCreateWindow(
            width,
            height,
            title, 0, 0
        ).also { window ->
            GLFW.glfwSetCursorPosCallback(window) { _, xPos: Double, yPos: Double ->
                onCursorEvent(xPos, yPos)
            }
            GLFW.glfwSetMouseButtonCallback(window) { _, button: Int, action: Int, mods: Int ->
                onMouseButtonEvent(button, action, mods)
            }
            GLFW.glfwSetKeyCallback(window) { _, key: Int, code: Int, action: Int, mods: Int ->
                onKeyBoardEvent(key, code, action, mods)
            }
            GLFW.glfwSetWindowSizeCallback(window) { _, width: Int, height: Int ->
                onResize(width, height)
            }

            setup(window)
            while (!GLFW.glfwWindowShouldClose(window))
                this.draw()
        }
    }

    open fun setup(window: Long) {
        GLFW.glfwMakeContextCurrent(window)
        GLFW.glfwShowWindow(window)
        GLFW.glfwSetCursorPos(
            window,
            (width / 2).toDouble(),
            (height / 2).toDouble()
        )
        GL.createCapabilities()
        GLUtil.setupDebugMessageCallback(PrintStream(object : OutputStream() {
            override fun write(char: Int) {
                print(char.toChar())
            }
        }))
    }

    abstract fun draw()

    abstract fun onCursorEvent(xPos: Double, yPos: Double)

    abstract fun onMouseButtonEvent(button: Int, action: Int, mods: Int)

    abstract fun onKeyBoardEvent(key: Int, code: Int, action: Int, mods: Int)

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