package juniojsv.engine.features.ui

import juniojsv.engine.features.context.WindowContext

interface IImGuiLayout {
    fun setup(context: WindowContext)
    fun render(context: WindowContext)
}