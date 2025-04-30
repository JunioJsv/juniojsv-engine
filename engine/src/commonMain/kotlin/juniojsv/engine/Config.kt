package juniojsv.engine

interface IConfig {
    var isDebug: Boolean
    var isShadowsEnabled: Boolean
}

expect object Config : IConfig