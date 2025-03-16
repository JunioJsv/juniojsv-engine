package juniojsv.engine.features.context

import org.lwjgl.glfw.GLFW

interface ITimeContext {
    val deltaInSeconds: Double
    val elapsedInSeconds: Double
}

class TimeContext : ITimeContext {
    private var lastRenderElapsedInSeconds = elapsedInSeconds
    override var deltaInSeconds = 0.0
        private set

    override val elapsedInSeconds: Double
        get() = GLFW.glfwGetTime()

    fun onPreRender() {
        val elapsedInSeconds = this.elapsedInSeconds
        deltaInSeconds = elapsedInSeconds - lastRenderElapsedInSeconds
        lastRenderElapsedInSeconds = elapsedInSeconds
    }
}