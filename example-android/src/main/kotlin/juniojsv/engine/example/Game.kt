package juniojsv.engine.example

import android.content.Context
import android.view.MotionEvent
import juniojsv.engine.example.scenes.main.MainScene
import juniojsv.engine.example.utils.ResourcesCommon
import juniojsv.engine.features.render.RenderPipeline
import juniojsv.engine.features.scene.Scene
import juniojsv.engine.features.utils.Resources
import juniojsv.engine.features.window.AndroidWindow

class Game(applicationContext: Context) : AndroidWindow(applicationContext), RenderPipeline.ICallbacks {
    private lateinit var scene: Scene
    private lateinit var pipeline: RenderPipeline

    private val camera
        get() = context.camera.instance

    override fun onCreate() {
        super.onCreate()
        Resources.registry(ResourcesCommon)
        pipeline = RenderPipeline(this)
        scene = MainScene()
    }

    override fun onRenderScene() {
        scene.render(context)
    }

    override fun onRenderDebugger() {
        context.debugger.render(context)
    }

    override fun onRender() {
        pipeline.render(this)
    }

    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isFirstTouch = true

    fun onTouchEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                isFirstTouch = false
            }

            MotionEvent.ACTION_MOVE -> {
                if (isFirstTouch) return

                val touchX = event.x
                val touchY = event.y
                if (touchX == lastTouchX && touchY == lastTouchY) return

                val deltaX = touchX - lastTouchX
                val deltaY = touchY - lastTouchY

                val maxDelta = 100f
                val clampedDeltaX = deltaX.coerceIn(-maxDelta, maxDelta)
                val clampedDeltaY = deltaY.coerceIn(-maxDelta, maxDelta)

                val sensitivity = 0.2f
                camera.rotate(clampedDeltaX * sensitivity, clampedDeltaY * sensitivity)

                lastTouchX = touchX
                lastTouchY = touchY
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isFirstTouch = true
            }
        }
    }

}