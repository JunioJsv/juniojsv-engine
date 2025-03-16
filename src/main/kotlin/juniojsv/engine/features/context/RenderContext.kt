package juniojsv.engine.features.context

import juniojsv.engine.features.entity.Light
import juniojsv.engine.features.entity.debugger.IDebugBeing
import org.lwjgl.opengl.GL11

class RenderContext {
    var ambientLight: Light? = null
    var resolutionScale: Float = 1f
    val debugBeings: MutableList<IDebugBeing> = mutableListOf()

    fun onPreRender() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        debugBeings.clear()
    }
}