package juniojsv.engine.features.utils

class Cooldown(private val seconds: Double, private val callback: () -> Unit) {
    private var nextInvoke = 0.0

    fun invoke() {
        // Current time in seconds
        val currentTime = System.currentTimeMillis() / 1000.0
        if (currentTime > nextInvoke) {
            nextInvoke = currentTime + seconds
            callback()
        }
    }
}