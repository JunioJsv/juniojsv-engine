package juniojsv.engine.platforms

enum class SupportedPlatform {
    JVM_WINDOWS,
    ANDROID,
}

expect val platform: SupportedPlatform