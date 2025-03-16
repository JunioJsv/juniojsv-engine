package juniojsv.engine.features.scene

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.entity.IRender

interface IScene : IRender {
    fun setup(context: IWindowContext)
    override fun render(context: IWindowContext)
}