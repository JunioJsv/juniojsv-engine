package juniojsv.engine.features.entity

import juniojsv.engine.features.context.WindowContext

interface IRender {
    fun render(context: WindowContext)
}