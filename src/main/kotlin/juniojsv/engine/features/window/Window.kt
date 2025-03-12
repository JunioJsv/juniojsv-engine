package juniojsv.engine.features.window

import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import juniojsv.engine.features.context.WindowContext
import juniojsv.engine.features.utils.OpenGLDebugCallback
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL43
import kotlin.properties.Delegates


abstract class Window(private var resolution: Resolution) {
    abstract val title: String

    private val glslVersion = "#version 330"

    var id by Delegates.notNull<Long>()
        private set

    private lateinit var context: WindowContext

    private val imGuiGlfw = ImGuiImplGlfw()
    private val imGuiGl3 = ImGuiImplGl3()

    private fun getWidth(): Int = resolution.width
    private fun getHeight(): Int = resolution.height
    fun getResolution(): Resolution = resolution
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

        id = GLFW.glfwCreateWindow(
            getWidth(),
            getHeight(),
            title, 0, 0
        )
        context = WindowContext(this)
        setup()
        while (!GLFW.glfwWindowShouldClose(id)) {
            with(context) {
                onPreRender()
                onRender(this)
                onPostRender()
            }
        }
        dispose()
    }

    private fun setup() {
        GLFW.glfwSetCursorPosCallback(id) { _, x: Double, y: Double ->
            onCursorOffsetEvent(
                context,
                Math.toRadians(x - resolution.width / 2),
                Math.toRadians(y - resolution.height / 2)
            )
        }
        GLFW.glfwSetMouseButtonCallback(id) { _, button: Int, action: Int, mods: Int ->
            onMouseButtonEvent(button, action, mods)
        }
        GLFW.glfwSetKeyCallback(id) { _, key: Int, code: Int, action: Int, mods: Int ->
            onKeyBoardEvent(context, key, code, action, mods)
        }
        GLFW.glfwSetWindowSizeCallback(id) { _, width: Int, height: Int ->
            onResize(width, height)
        }
        GLFW.glfwMakeContextCurrent(id)
        GLFW.glfwShowWindow(id)
        GLFW.glfwSetCursorPos(
            id,
            (getWidth() / 2).toDouble(),
            (getHeight() / 2).toDouble()
        )
        GLFW.glfwSetWindowSizeCallback(id) { _, width: Int, height: Int ->
            onResize(width, height)
        }
        GL.createCapabilities()
        if (GL.getCapabilities().GL_ARB_debug_output) {
            GL11.glEnable(GL43.GL_DEBUG_OUTPUT)
            GL43.glDebugMessageCallback(OpenGLDebugCallback.create(), 0)
            GL11.glEnable(GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS)
        }

        ImGui.createContext()
        ImGui.getIO().addConfigFlags(ImGuiConfigFlags.ViewportsEnable)
        imGuiGlfw.init(id, true)
        imGuiGl3.init(glslVersion)

        onCreate(context)
    }

    private fun dispose() {
        imGuiGl3.dispose()
        imGuiGlfw.dispose()
        ImGui.destroyContext()
        Callbacks.glfwFreeCallbacks(id)
        GLFW.glfwDestroyWindow(id)
        GLFW.glfwTerminate()
    }

    open fun onCreate(context: WindowContext) {}

    abstract fun onRender(context: WindowContext)

    abstract fun onCursorOffsetEvent(context: WindowContext, x: Double, y: Double)

    abstract fun onMouseButtonEvent(button: Int, action: Int, mods: Int)

    abstract fun onKeyBoardEvent(context: WindowContext, key: Int, code: Int, action: Int, mods: Int)

    private fun onResize(width: Int, height: Int) {
        GL11.glViewport(0, 0, width, height)
        resolution = Resolution(width, height)
    }
}