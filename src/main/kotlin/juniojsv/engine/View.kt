package juniojsv.engine

import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GLUtil
import java.io.OutputStream
import java.io.PrintStream

abstract class View(
    title: String,
    var width: Int = 800,
    var height: Int = 600
) {
    protected var window: Long = 0
        private set

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
            this.window = window
            GLFW.glfwSetCursorPosCallback(window) { _, xPosition: Double, yPosition: Double ->
                onCursorEvent(xPosition, yPosition)
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

            setup()
            while (!GLFW.glfwWindowShouldClose(window)) {
                draw()
            }
        }
    }

    open fun setup() {
        GLFW.glfwMakeContextCurrent(window)
        GLFW.glfwShowWindow(window)
        GLFW.glfwSetCursor(
            window, GLFW.glfwCreateStandardCursor(GLFW.GLFW_CROSSHAIR_CURSOR)
        )
        GLFW.glfwSetCursorPos(
            window,
            (width / 2).toDouble(),
            (height / 2).toDouble()
        )
        GL.createCapabilities()
        GLUtil.setupDebugMessageCallback(PrintStream(object : OutputStream() {
            override fun write(char: Int) {}
        }))
    }

    abstract fun draw()

    abstract fun onCursorEvent(positionX: Double, positionY: Double)

    abstract fun onMouseButtonEvent(button: Int, action: Int, mods: Int)

    abstract fun onKeyBoardEvent(key: Int, code: Int, action: Int, mods: Int)

    open fun onResize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }
}