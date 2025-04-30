package juniojsv.engine.features.context

import juniojsv.engine.features.utils.FrameTimer

interface ITimeContext {
    /**
     * Represents the time elapsed since the last frame was rendered, in seconds.
     * This value is typically used for time-dependent calculations, such as animations,
     * physics simulations, and updates, to ensure frame-independent behavior.
     */
    val deltaTime: Double

    /**
     * Represents the current time in seconds since the application started.
     *
     * This value is typically updated each frame and provides a continuous measure
     * of the runtime. It can be used for time-dependent calculations
     * or animations.
     */
    val current: Double

    /**
     * Represents the current frames per second (FPS) rate.
     * This value indicates how many frames are being rendered per second
     * and is often used as a performance metric for rendering or real-time simulation.
     */
    val fps: Double
}

class TimeContext : ITimeContext {
    private val timer = FrameTimer()

    override val deltaTime: Double
        get() = timer.deltaTime

    override val current: Double
        get() = System.nanoTime() / 1_000_000_000.0

    override val fps: Double get() = timer.averageFps

    fun onPreRender() {
        timer.update()
    }
}