package juniojsv.engine.features.window

import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import juniojsv.engine.Config
import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.context.WindowContext
import juniojsv.engine.features.utils.OpenGLDebugCallback
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL43
import kotlin.properties.Delegates
import kotlin.system.exitProcess

abstract class Window(resolution: Resolution) {
    abstract val title: String

    var resolution = resolution
        private set
    private val glslVersion = "#version 330"

    var id by Delegates.notNull<Long>()
        private set

    lateinit var context: IWindowContext
        private set

    val imGuiGlfw = ImGuiImplGlfw()
    val imGuiGl3 = ImGuiImplGl3()

    fun init() {
        GLFWErrorCallback.createPrint(System.err).set()
        if (!GLFW.glfwInit())
            throw Exception("Can't init GLFW")

        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE)
        if (!Config.isWindowResizable)
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE)

        id = GLFW.glfwCreateWindow(
            resolution.width,
            resolution.height,
            title, 0, 0
        )
        setup()
        while (!GLFW.glfwWindowShouldClose(id)) (context as WindowContext).apply {
            onPreRender()
            onRender()
            onPostRender()
        }
        dispose()
    }

    private fun setup() {
        GLFW.glfwMakeContextCurrent(id)
        GLFW.glfwSwapInterval(if (Config.isVsyncEnabled) 1 else 0)
        GL.createCapabilities()
        if (GL.getCapabilities().GL_ARB_debug_output) {
            GL11.glEnable(GL43.GL_DEBUG_OUTPUT)
            GL43.glDebugMessageCallback(OpenGLDebugCallback.create(), 0)
            GL11.glEnable(GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS)
        }
        context = WindowContext(this)
        GLFW.glfwSetCursorPosCallback(id) { _, x: Double, y: Double ->
            onCursorOffsetEvent(
                Math.toRadians(x - resolution.width / 2),
                Math.toRadians(y - resolution.height / 2)
            )
        }
        GLFW.glfwSetMouseButtonCallback(id) { _, button: Int, action: Int, mods: Int ->
            onMouseButtonEvent(button, action, mods)
        }
        GLFW.glfwSetKeyCallback(id) { _, key: Int, code: Int, action: Int, mods: Int ->
            onKeyBoardEvent(key, code, action, mods)
        }
        GLFW.glfwSetWindowSizeCallback(id) { _, width: Int, height: Int ->
            if (width > 0 && height > 0)
                onResize(width, height)
        }
        GLFW.glfwShowWindow(id)
        GLFW.glfwSetCursorPos(
            id,
            (resolution.width / 2).toDouble(),
            (resolution.height / 2).toDouble()
        )
        ImGui.createContext()
        ImGui.getIO().addConfigFlags(ImGuiConfigFlags.ViewportsEnable)
        imGuiGlfw.init(id, true)
        imGuiGl3.init(glslVersion)

        onCreate()
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glCullFace(GL11.GL_BACK)
        GL11.glClearColor(0f, 0f, 0f, 1f)
    }

    private fun dispose() {
        context.dispose()
        imGuiGl3.dispose()
        imGuiGlfw.dispose()
        ImGui.destroyContext()
        Callbacks.glfwFreeCallbacks(id)
        GLFW.glfwDestroyWindow(id)
        GLFW.glfwTerminate()
        exitProcess(0)
    }

    protected open fun onCreate() {}

    protected abstract fun onRender()

    protected abstract fun onCursorOffsetEvent(x: Double, y: Double)

    protected abstract fun onMouseButtonEvent(button: Int, action: Int, mods: Int)

    protected abstract fun onKeyBoardEvent(key: Int, code: Int, action: Int, mods: Int)

    protected open fun onResize(width: Int, height: Int) {
        GL11.glViewport(0, 0, width, height)
        resolution = Resolution(width, height)
    }
}