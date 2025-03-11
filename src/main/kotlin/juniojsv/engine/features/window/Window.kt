package juniojsv.engine.features.window

import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11


data class WindowContext(val id: Long)

abstract class Window(private var resolution: Resolution) {
    abstract val title: String

    private val glslVersion = "#version 330"

    private lateinit var windowContext: WindowContext

    private lateinit var renderContext: RenderContext

    private val imGuiGlfw = ImGuiImplGlfw()
    private val imGuiGl3 = ImGuiImplGl3()

    private fun getWidth(): Int = resolution.width
    private fun getHeight(): Int = resolution.height
    fun getResolution(): Resolution = resolution
    fun getWindowContext() = windowContext
    fun getImGuiGlfw() = imGuiGlfw
    fun getImGuiGl3() = imGuiGl3

    fun init() {
        GLFWErrorCallback.createPrint(System.err).set()
        if (!GLFW.glfwInit())
            throw Exception("Can't init GLFW")

        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE)

        GLFW.glfwCreateWindow(
            getWidth(),
            getHeight(),
            title, 0, 0
        ).also { window ->
            windowContext = WindowContext(window)
            renderContext = RenderContext(this)
            setup()
            while (!GLFW.glfwWindowShouldClose(window)) {
                with(renderContext) {
                    onInitRender()
                    onRender(this)
                    onPostRender()
                }
            }
            dispose()
        }
    }

    private fun setup() {
        GLFW.glfwSetCursorPosCallback(windowContext.id) { _, x: Double, y: Double ->
            onCursorOffsetEvent(
                renderContext,
                Math.toRadians(x - resolution.width / 2),
                Math.toRadians(y - resolution.height / 2)
            )
        }
        GLFW.glfwSetMouseButtonCallback(windowContext.id) { _, button: Int, action: Int, mods: Int ->
            onMouseButtonEvent(button, action, mods)
        }
        GLFW.glfwSetKeyCallback(windowContext.id) { _, key: Int, code: Int, action: Int, mods: Int ->
            onKeyBoardEvent(renderContext, key, code, action, mods)
        }
        GLFW.glfwSetWindowSizeCallback(windowContext.id) { _, width: Int, height: Int ->
            onResize(width, height)
        }
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
//        GLUtil.setupDebugMessageCallback(System.out)

        ImGui.createContext()
        ImGui.getIO().addConfigFlags(ImGuiConfigFlags.ViewportsEnable)
        imGuiGlfw.init(windowContext.id, true)
        imGuiGl3.init(glslVersion)

        onCreate(renderContext)
    }

    private fun dispose() {
        imGuiGl3.dispose()
        imGuiGlfw.dispose()
        ImGui.destroyContext()
        Callbacks.glfwFreeCallbacks(windowContext.id)
        GLFW.glfwDestroyWindow(windowContext.id)
        GLFW.glfwTerminate()
    }

    open fun onCreate(context: IRenderContext) {}

    abstract fun onRender(context: IRenderContext)

    abstract fun onCursorOffsetEvent(context: IRenderContext, x: Double, y: Double)

    abstract fun onMouseButtonEvent(button: Int, action: Int, mods: Int)

    abstract fun onKeyBoardEvent(context: IRenderContext, key: Int, code: Int, action: Int, mods: Int)

    private fun onResize(width: Int, height: Int) {
        GL11.glViewport(0, 0, width, height)
        resolution = Resolution(width, height)
    }
}