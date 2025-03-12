package juniojsv.engine.features.context

import org.lwjgl.glfw.GLFW

class TimeContext {
    private var lastPreRender = 0.0
    var delta = 0.0
        private set

    fun onPreRender() {
        val time = getTime()
        delta = time - lastPreRender
        lastPreRender = time
    }

    private fun getTime(): Double {
        return GLFW.glfwGetTime()
    }
}