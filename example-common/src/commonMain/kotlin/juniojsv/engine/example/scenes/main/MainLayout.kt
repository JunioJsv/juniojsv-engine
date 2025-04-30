package juniojsv.engine.example.scenes.main

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.gui.GuiLayout

interface MainLayoutListener {
    fun didResolutionScaleChanged(scale: Float) {}
}

interface MainLayoutCallbacks {
    fun getSkyboxCount(): Int
    fun setSkybox(context: IWindowContext, index: Int)
    fun onGenerateObjects(count: Int, instanced: Boolean = false)
}

expect class MainLayout(callbacks: MainLayoutCallbacks) : GuiLayout {
    val callbacks: MainLayoutCallbacks
}
