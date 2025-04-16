package juniojsv.engine.features.context

import juniojsv.engine.features.utils.FrameTimer

interface ITimeContext {
    val deltaInSeconds: Double
    val elapsedInSeconds: Double
    val averageFps: Double
}

class TimeContext : ITimeContext {
    private val timer = FrameTimer()

    override val deltaInSeconds: Double
        get() = timer.deltaTime
    override val elapsedInSeconds: Double
        get() = System.nanoTime() / 1_000_000_000.0
    override val averageFps: Double get() = timer.averageFps


    fun onPreRender() {
        timer.update()
    }
}