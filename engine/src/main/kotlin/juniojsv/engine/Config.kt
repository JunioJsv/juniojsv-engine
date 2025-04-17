package juniojsv.engine

import juniojsv.engine.features.window.Resolution

object Config {
    var isDebug = false
    var isWindowResizable = true
    var isFullScreen = false
    var isVsyncEnabled = false
    var isShadowsEnabled = true
    var resolution: Resolution? = null
}