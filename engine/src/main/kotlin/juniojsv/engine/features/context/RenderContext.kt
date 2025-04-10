package juniojsv.engine.features.context

import juniojsv.engine.features.entity.Light
import juniojsv.engine.features.entity.debugger.IDebugBeing
import org.lwjgl.opengl.GL11

interface IRenderContext {
    var ambientLight: Light?
    var resolutionScale: Float
    var motionBlur: Float
    val debugBeings: MutableList<IDebugBeing>
}

class RenderContext : IRenderContext {
    override var ambientLight: Light? = null
    override var resolutionScale: Float = 1f
    override var motionBlur: Float = .5f
    override val debugBeings: MutableList<IDebugBeing> = mutableListOf()

    fun onPreRender() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        debugBeings.clear()
    }
}