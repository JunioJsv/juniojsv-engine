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
    internal abstract var gui: Gui

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
            var previousFrame = 0.0
            while (!GLFW.glfwWindowShouldClose(window)) {
                draw(GLFW.glfwGetTime() - previousFrame)
                previousFrame = GLFW.glfwGetTime()
            }

            channel("exit", null)
        }
    }

    open fun setup(window: Long) {
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
            override fun write(char: Int) {
                gui.console.print("${char.toChar()}")
            }
        }))
    }

    abstract fun draw(delta: Double)

    abstract fun onCursorEvent(xPos: Double, yPos: Double)

    abstract fun onMouseButtonEvent(button: Int, action: Int, mods: Int)

    abstract fun onKeyBoardEvent(key: Int, code: Int, action: Int, mods: Int)

    open fun onResize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    abstract fun channel(method: String, args: List<String>?): Any?
}