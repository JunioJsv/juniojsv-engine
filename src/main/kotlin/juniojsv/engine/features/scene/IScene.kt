package juniojsv.engine.features.scene

import juniojsv.engine.features.context.WindowContext

interface IScene {
    fun setup(context: WindowContext)
    fun render(context: WindowContext)
}