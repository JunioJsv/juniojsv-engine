package juniojsv.engine.features.entity

import juniojsv.engine.features.window.IRenderContext

interface IRender {
    fun render(context: IRenderContext)
}