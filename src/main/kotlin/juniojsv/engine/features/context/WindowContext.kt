package juniojsv.engine.features.context

import juniojsv.engine.features.window.Window
import org.lwjgl.glfw.GLFW

interface IWindowContext {
    val time: ITimeContext
    val camera: ICameraContext
    val gui: IGuiContext
    val render: IRenderContext
    val physics: IPhysicsContext
}

class WindowContext(private val window: Window) : IWindowContext {
    override val time = TimeContext()
    override val camera = CameraContext(window)
    override val gui = GuiContext(window)
    override val render = RenderContext()
    override val physics = PhysicsContext(window)

    fun onPreRender() {
        time.onPreRender()
        camera.onPreRender()
        render.onPreRender()
        physics.onPreRender()
    }

    fun onPostRender() {
        gui.onPostRender()
        camera.onPostRender()
        GLFW.glfwSwapBuffers(window.id)
        GLFW.glfwPollEvents()
    }
}