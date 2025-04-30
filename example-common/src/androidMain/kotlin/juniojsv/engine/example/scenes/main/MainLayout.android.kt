package juniojsv.engine.example.scenes.main

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.gui.GuiLayout
import juniojsv.engine.features.window.PlatformWindow

actual class MainLayout actual constructor(actual val callbacks: MainLayoutCallbacks) : GuiLayout() {
    override fun render(window: PlatformWindow) {

    }

    override fun render(context: IWindowContext) {

    }
}