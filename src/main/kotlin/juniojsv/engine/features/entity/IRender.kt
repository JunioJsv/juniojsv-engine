package juniojsv.engine.features.entity

import juniojsv.engine.features.context.WindowContext
import juniojsv.engine.features.utils.IDisposable

interface IRender : IDisposable {
    fun render(context: WindowContext)
}