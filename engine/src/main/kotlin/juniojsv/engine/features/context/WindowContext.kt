package juniojsv.engine.features.context

import juniojsv.engine.features.render.DebugShapeRender
import juniojsv.engine.features.utils.IDisposable
import juniojsv.engine.features.window.Window
import org.lwjgl.glfw.GLFW

interface IWindowContextListener {
    fun onPreRender(context: IWindowContext) {}
    fun onPostRender(context: IWindowContext) {}
}

interface IWindowContext : IDisposable {
    val time: ITimeContext
    val camera: ICameraContext
    val gui: IGuiContext
    val render: IRenderContext
    val physics: IPhysicsContext
    val debugger: DebugShapeRender

    fun addListener(listener: IWindowContextListener)
    fun removeListener(listener: IWindowContextListener)
}

class WindowContext(private val window: Window) : IWindowContext {
    override val time = TimeContext()
    override val camera = CameraContext(window)
    override val gui = GuiContext(window)
    override val render = RenderContext()
    override val physics = PhysicsContext()
    override val debugger: DebugShapeRender by lazy { DebugShapeRender() }

    init {
        physics.start()
    }

    private val listeners = mutableSetOf<IWindowContextListener>()

    override fun addListener(listener: IWindowContextListener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: IWindowContextListener) {
        listeners.remove(listener)
    }

    fun onPreRender() {
        time.onPreRender()
        camera.onPreRender()
        render.onPreRender()
        listeners.forEach { it.onPreRender(this) }
    }

    fun onPostRender() {
        gui.onPostRender()
        camera.onPostRender()
        listeners.forEach { it.onPostRender(this) }
        GLFW.glfwSwapBuffers(window.id)
        GLFW.glfwPollEvents()
    }

    override fun dispose() {
        physics.stop()
    }
}