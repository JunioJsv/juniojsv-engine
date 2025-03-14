package juniojsv.engine.features.context

import juniojsv.engine.features.entity.BaseBeing
import juniojsv.engine.features.entity.Light

class RenderState {
    var ambientLight: Light? = null
    var resolutionScale: Float = 1f
    val beings: MutableList<BaseBeing> = mutableListOf()
}