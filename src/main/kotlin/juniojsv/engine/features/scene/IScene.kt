package juniojsv.engine.features.scene

import juniojsv.engine.features.context.WindowContext
import juniojsv.engine.features.entity.IRender

interface IScene : IRender {
    fun setup(context: WindowContext)
    override fun render(context: WindowContext)
}