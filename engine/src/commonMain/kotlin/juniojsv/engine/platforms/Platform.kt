package juniojsv.engine.platforms

enum class SupportedPlatform {
    JVM_WINDOWS,
    ANDROID,
}

expect val platform: SupportedPlatform

object Platform {
    val isJvm get() = platform == SupportedPlatform.JVM_WINDOWS
    val isWindows get() = platform == SupportedPlatform.JVM_WINDOWS
    val isAndroid get() = platform == SupportedPlatform.ANDROID
}