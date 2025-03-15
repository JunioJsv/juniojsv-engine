package juniojsv.engine.features.utils

import juniojsv.engine.features.context.WindowContext
import org.lwjgl.glfw.GLFW

class KeyboardHandler {
    private val actions = mutableMapOf<Int, (delta: Double) -> Unit>()
    private val pressed = mutableSetOf<Int>()

    fun setKeyAction(key: Int, action: (delta: Double) -> Unit) {
        actions[key] = action
    }

    fun handle(key: Int, action: Int) {
        if (action == GLFW.GLFW_RELEASE) pressed.remove(key)
        if (action == GLFW.GLFW_PRESS) pressed.add(key)
    }

    fun pump(context: WindowContext) {
        if (pressed.isNotEmpty()) {
            for (key in pressed) actions[key]?.invoke(context.time.deltaInSeconds)
        }
    }
}