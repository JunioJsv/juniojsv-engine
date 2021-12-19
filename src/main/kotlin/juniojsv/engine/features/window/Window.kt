package juniojsv.engine.features.window

import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GLUtil
import java.io.OutputStream
import java.io.PrintStream

data class WindowContext(val id: Long)

abstract class Window(private var resolution: Resolution) {
    abstract val title: String

    private lateinit var windowContext: WindowContext

    private val renderContext = RenderContext()

    private fun getWidth(): Int = resolution.width
    private fun getHeight(): Int = resolution.height
    fun getResolution(): Resolution = resolution
    fun getWindowContext() = windowContext

    fun init() {
        if (!GLFW.glfwInit())
            throw Exception("Can't init GLFW")

        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE)

        GLFW.glfwCreateWindow(
            getWidth(),
            getHeight(),
            title, 0, 0
        ).also { window ->
            windowContext = WindowContext(window)
            GLFW.glfwSetCursorPosCallback(window) { _, xPosition: Double, yPosition: Double ->
                onCursorOffsetEvent(
                    renderContext,
                    Math.toRadians(xPosition - resolution.width / 2),
                    Math.toRadians(yPosition - resolution.height / 2)
                )
            }
            GLFW.glfwSetMouseButtonCallback(window) { _, button: Int, action: Int, mods: Int ->
                onMouseButtonEvent(button, action, mods)
            }
            GLFW.glfwSetKeyCallback(window) { _, key: Int, code: Int, action: Int, mods: Int ->
                onKeyBoardEvent(renderContext, key, code, action, mods)
            }
            GLFW.glfwSetWindowSizeCallback(window) { _, width: Int, height: Int ->
                onResize(width, height)
            }

            setup()
            while (!GLFW.glfwWindowShouldClose(window)) {
                with(renderContext) {
                    onInitFrame()
                    draw(this)
                    onPostFrame()
                }
                GLFW.glfwSwapBuffers(windowContext.id)
                GLFW.glfwPollEvents()
            }
        }
    }

    private fun setup() {
        GLFW.glfwMakeContextCurrent(windowContext.id)
        GLFW.glfwShowWindow(windowContext.id)
        GLFW.glfwSetCursorPos(
            windowContext.id,
            (getWidth() / 2).toDouble(),
            (getHeight() / 2).toDouble()
        )
        GLFW.glfwSetWindowSizeCallback(windowContext.id) { _, width: Int, height: Int ->
            onResize(width, height)
        }
        GL.createCapabilities()
        GLUtil.setupDebugMessageCallback(PrintStream(object : OutputStream() {
            override fun write(char: Int) {
                print(char.toChar())
            }
        }))
        onCreate()
    }

    open fun onCreate() {}

    abstract fun draw(context: IRenderContext)

    abstract fun onCursorOffsetEvent(context: IRenderContext, offsetX: Double, offsetY: Double)

    abstract fun onMouseButtonEvent(button: Int, action: Int, mods: Int)

    abstract fun onKeyBoardEvent(context: IRenderContext, key: Int, code: Int, action: Int, mods: Int)

    private fun onResize(width: Int, height: Int) {
        GL11.glViewport(0, 0, width, height)
        resolution = Resolution(width, height)
    }
}