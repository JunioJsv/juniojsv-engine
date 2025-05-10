package juniojsv.engine.features.context

import juniojsv.engine.features.render.DebugShapeRender
import juniojsv.engine.features.utils.IDisposable
import juniojsv.engine.features.window.PlatformWindow

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

open class WindowContext(window: PlatformWindow) : IWindowContext {
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

    open fun onPreRender() {
        time.onPreRender()
        camera.onPreRender()
        render.onPreRender()
        listeners.forEach { it.onPreRender(this) }
    }

    open fun onPostRender() {
        gui.onPostRender()
        camera.onPostRender()
        listeners.forEach { it.onPostRender(this) }
    }

    override fun dispose() {
        physics.stop()
    }
}