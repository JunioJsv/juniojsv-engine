package juniojsv.engine.features.context

import org.lwjgl.glfw.GLFW

class TimeContext {
    private var lastRenderElapsedInSeconds = elapsedInSeconds
    var deltaInSeconds = 0.0
        private set

    val elapsedInSeconds: Double
        get() = GLFW.glfwGetTime()

    fun onPreRender() {
        val elapsedInSeconds = this.elapsedInSeconds
        deltaInSeconds = elapsedInSeconds - lastRenderElapsedInSeconds
        lastRenderElapsedInSeconds = elapsedInSeconds
    }
}