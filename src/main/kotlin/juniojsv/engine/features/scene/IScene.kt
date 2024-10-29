package juniojsv.engine.features.scene

import juniojsv.engine.features.window.IRenderContext

interface IScene {
    fun setup(context: IRenderContext)
    fun render(context: IRenderContext)
}