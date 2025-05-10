package juniojsv.engine.features.gui

import juniojsv.engine.features.render.Render
import juniojsv.engine.features.window.PlatformWindow

abstract class GuiLayout : Render() {
    abstract fun render(window: PlatformWindow)
}