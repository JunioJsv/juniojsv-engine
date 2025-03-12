package juniojsv.engine.features.context

import juniojsv.engine.features.window.Window
import org.lwjgl.glfw.GLFW

class WindowContext(private val window: Window) {
    val time = TimeContext()
    val camera = CameraContext(window)
    val gui = GuiContext(window)
    val render = RenderContext()


    fun onPreRender() {
        time.onPreRender()
        camera.onPreRender()
        render.onPreRender()
    }

    fun onPostRender() {
        gui.onPostRender(this)
        GLFW.glfwSwapBuffers(window.id)
        GLFW.glfwPollEvents()
    }
}