package juniojsv.engine.features.gui

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.entity.IRender

interface IImGuiLayout : IRender {
    fun setup(context: IWindowContext)
    override fun render(context: IWindowContext)
}