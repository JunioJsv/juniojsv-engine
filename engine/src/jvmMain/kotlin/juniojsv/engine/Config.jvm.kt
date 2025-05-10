package juniojsv.engine

import juniojsv.engine.features.window.Resolution

actual object Config : JvmConfig()

open class JvmConfig : IConfig {
    override var isDebug = false
    override var isShadowsEnabled = true
    var resolution: Resolution? = null
    var isWindowResizable = true
    var isFullScreen = false
    var isVsyncEnabled = false
}