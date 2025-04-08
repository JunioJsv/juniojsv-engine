package juniojsv.engine.features.utils

import java.util.*

class Debounce(private val seconds: Double = .2, private val callback: () -> Unit) {
    private var timer = Timer()
    private var task: TimerTask? = null

    operator fun invoke() {
        task?.cancel()
        task = object : TimerTask() {
            override fun run() {
                callback()
            }
        }
        timer.schedule(task, (seconds * 1000).toLong())
    }

}