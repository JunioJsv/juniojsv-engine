package juniojsv.engine.features.window

import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import juniojsv.engine.Config
import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.context.WindowContext
import juniojsv.engine.features.utils.OpenGLDebugCallback
import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.constants.*
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import kotlin.properties.Delegates
import kotlin.system.exitProcess

class JvmWindowContext(private val window: JvmWindow) : WindowContext(window) {
    override fun onPostRender() {
        super.onPostRender()
        GLFW.glfwSwapBuffers(window.id)
        GLFW.glfwPollEvents()
    }
}

abstract class JvmWindow(private val title: String) : PlatformWindow() {
    final override lateinit var resolution: Resolution
        private set

    final override lateinit var context: IWindowContext
        private set

    private val glslVersion = "#version 330"

    var id by Delegates.notNull<Long>()
        private set

    val imGuiGlfw = ImGuiImplGlfw()
    val imGuiGl3 = ImGuiImplGl3()

    override fun setup() {
        GLFWErrorCallback.createPrint(System.err).set()
        if (!GLFW.glfwInit())
            throw Exception("Can't init GLFW")

        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE)
        if (!Config.isWindowResizable)
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE)

        val isMaximizedWindow = !Config.isFullScreen && Config.resolution == null
        if (isMaximizedWindow)
            GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE)

        val monitor: Long = GLFW.glfwGetPrimaryMonitor()
        if (monitor == 0L) throw Exception("Can't find primary monitor")

        resolution = Config.resolution
            ?: GLFW.glfwGetVideoMode(monitor)?.let { Resolution(it.width(), it.height()) }
                    ?: Resolution(800, 600)

        id = GLFW.glfwCreateWindow(
            resolution.width,
            resolution.height,
            title,
            if (Config.isFullScreen) monitor else 0,
            0
        )

        if (isMaximizedWindow) {
            GLFW.glfwSetWindowSizeLimits(
                id,
                800, 600,
                GLFW.GLFW_DONT_CARE,
                GLFW.GLFW_DONT_CARE
            )
            GLFW.glfwMaximizeWindow(id)
            val width = intArrayOf(0)
            val height = intArrayOf(0)
            GLFW.glfwGetFramebufferSize(id, width, height)
            resolution = Resolution(width[0], height[0])
        }

        GLFW.glfwMakeContextCurrent(id)
        GLFW.glfwSwapInterval(if (Config.isVsyncEnabled) 1 else 0)
        val capabilities = GL.createCapabilities()
        if (capabilities.GL_ARB_debug_output) {
            GL.glEnable(GL_DEBUG_OUTPUT)
            GL.glDebugMessageCallback(OpenGLDebugCallback.create())
            GL.glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS)
        }
        context = JvmWindowContext(this)
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
    }

    override fun isRunning(): Boolean = !GLFW.glfwWindowShouldClose(id)

    override fun dispose() {
        context.dispose()
        imGuiGl3.dispose()
        imGuiGlfw.dispose()
        ImGui.destroyContext()
        Callbacks.glfwFreeCallbacks(id)
        GLFW.glfwDestroyWindow(id)
        GLFW.glfwTerminate()
        exitProcess(0)
    }

    protected abstract fun onCursorOffsetEvent(x: Double, y: Double)

    protected abstract fun onMouseButtonEvent(button: Int, action: Int, mods: Int)

    protected abstract fun onKeyBoardEvent(key: Int, code: Int, action: Int, mods: Int)

    protected open fun onResize(width: Int, height: Int) {
        GL.glViewport(0, 0, width, height)
        resolution = Resolution(width, height)
    }
}