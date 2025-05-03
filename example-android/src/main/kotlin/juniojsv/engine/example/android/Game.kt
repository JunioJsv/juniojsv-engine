package juniojsv.engine.example.android

import android.content.Context
import android.view.MotionEvent
import juniojsv.engine.example.scenes.main.MainScene
import juniojsv.engine.example.utils.ResourcesCommon
import juniojsv.engine.features.render.RenderPipeline
import juniojsv.engine.features.scene.Scene
import juniojsv.engine.features.utils.MovementDirection
import juniojsv.engine.features.utils.Resources
import juniojsv.engine.features.window.AndroidWindow
import org.joml.Vector2f

open class Game(applicationContext: Context) : AndroidWindow(applicationContext),
    RenderPipeline.ICallbacks {

    private lateinit var scene: Scene
    private lateinit var pipeline: RenderPipeline
    private val movement = Vector2f()
    private val buttons = mutableSetOf<String>()

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
        if (movement.x != 0f || movement.y != 0f)
            camera.move(movement.x, movement.y)

        if (buttons.contains("B"))
            camera.move(setOf(MovementDirection.UP))

        (scene as? MainScene)?.apply {
            if (buttons.contains("Y"))
                onGenerateObjects(100, false)

            if (buttons.contains("X")) {
                val nextSkybox = (getCurrentSkyboxIndex() + 1) % getSkyboxCount()
                setSkybox(context, nextSkybox)
            }
        }
        buttons.clear()
    }

    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isFirstTouch = true

    fun onButtonEvent(label: String) {
        buttons.add(label)
    }

    fun onJoystickEvent(x: Float, y: Float) {
        movement.x = x
        movement.y = y
    }

    fun onTouchEvent(id: Long, x: Float, y: Float, action: Int) {
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = x
                lastTouchY = y
                isFirstTouch = false
            }

            MotionEvent.ACTION_MOVE -> {
                if (isFirstTouch) return

                val touchX = x
                val touchY = y
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