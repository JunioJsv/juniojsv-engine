package juniojsv.engine

actual object Config : AndroidConfig()

open class AndroidConfig : IConfig {
    override var isDebug: Boolean = false
    override var isShadowsEnabled: Boolean = false
}