package juniojsv.engine.features.context

import juniojsv.engine.features.gui.GuiLayout
import juniojsv.engine.features.window.PlatformWindow

interface IGuiContext {
    var layout: GuiLayout?
}

class GuiContext(private val window: PlatformWindow) : IGuiContext {
    override var layout: GuiLayout? = null

    fun onPostRender() {
        layout?.render(window)
    }
}