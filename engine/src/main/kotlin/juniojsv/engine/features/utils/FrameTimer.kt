package juniojsv.engine.features.utils

import kotlin.math.roundToInt

/**
 * Manages frame timing, calculating delta time and average Frames Per Second (FPS).
 *
 * Call [update] at the beginning of each frame or update cycle.
 * Access [deltaTime] for the time elapsed since the last update in seconds.
 * Access [averageFps] for the average FPS calculated over the specified interval.
 *
 * @param fpsUpdateInterval The interval in seconds over which to calculate the average FPS. Defaults to 1.0 second.
 */
class FrameTimer(private val fpsUpdateInterval: Double = 1.0) {

    /**
     * The time elapsed between the last two calls to [update], in seconds.
     */
    var deltaTime: Double = 0.0
        private set

    /**
     * The average Frames Per Second calculated over the [fpsUpdateInterval].
     */
    var averageFps: Double = 0.0
        private set

    /**
     * Gets the time of the last update in seconds.
     *
     * This property converts the internal `lastTime` value from nanoseconds to seconds
     * by dividing it by 1,000,000,000.0.
     * Useful for debugging or monitoring purposes to assess the timer's state.
     */
    val lastUpdate get() = lastTime / 1_000_000_000.0

    private var lastTime: Long = System.nanoTime()
    private var frameCount: Int = 0
    private var accumulatedDeltaTimeInternal: Double = 0.0

    init {
        reset()
    }

    /**
     * Updates the timer. Should be called once per frame, ideally at the beginning.
     * Calculates [deltaTime] for the current frame and updates [averageFps] periodically.
     *
     * @return `true` if the average FPS was recalculated during this update, `false` otherwise.
     */
    fun update(): Boolean {
        val now = System.nanoTime()
        // Calculate delta time in seconds (as a Float)
        val elapsedNanos = now - lastTime
        // Prevent division by zero or negative time if clock glitches
        deltaTime = if (elapsedNanos > 0) elapsedNanos / 1_000_000_000.0 else 0.0
        lastTime = now // Update lastTime for the next frame calculation

        // Accumulate data for average FPS calculation
        accumulatedDeltaTimeInternal += deltaTime
        frameCount++

        var fpsWasUpdated = false
        // Check if the update interval has been reached
        if (accumulatedDeltaTimeInternal >= fpsUpdateInterval) {
            // Calculate average FPS for the completed interval
            averageFps = if (accumulatedDeltaTimeInternal > 0) {
                frameCount / accumulatedDeltaTimeInternal
            } else {
                0.0 // Avoid division by zero
            }

            // Reset counters for the next interval
            frameCount = 0
            // Carry over any excess time to the next interval for accuracy
            accumulatedDeltaTimeInternal -= fpsUpdateInterval
            // Ensure it doesn't become significantly negative due to float inaccuracies
            if (accumulatedDeltaTimeInternal < 0) accumulatedDeltaTimeInternal = 0.0

            fpsWasUpdated = true
        }
        return fpsWasUpdated
    }

    /**
     * Resets the timer's internal state, including [lastTime], [deltaTime], and FPS counters.
     * Useful when starting or restarting a loop.
     */
    fun reset() {
        lastTime = System.nanoTime()
        deltaTime = 0.0
        averageFps = 0.0
        frameCount = 0
        accumulatedDeltaTimeInternal = 0.0
    }

    /**
     * Convenience function to get the average FPS rounded to the nearest integer.
     */
    fun getAverageFpsRounded(): Int = averageFps.roundToInt()
}