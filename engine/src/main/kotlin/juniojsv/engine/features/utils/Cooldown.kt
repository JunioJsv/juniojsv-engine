package juniojsv.engine.features.utils

import org.lwjgl.glfw.GLFW

class Cooldown(private val seconds: Double, private val callback: () -> Unit) {
    private var nextInvoke = 0.0

    fun invoke() {
        val currentTime = GLFW.glfwGetTime()
        if (currentTime > nextInvoke) {
            nextInvoke = currentTime + seconds
            callback()
        }
    }
}