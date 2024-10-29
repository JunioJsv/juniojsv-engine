package juniojsv.engine.features.ui

import juniojsv.engine.features.window.IRenderContext

interface IImGuiLayout {
    fun setup(context: IRenderContext)
    fun render(context: IRenderContext)
}