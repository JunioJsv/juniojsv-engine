package juniojsv.engine.example

import imgui.ImGui
import juniojsv.engine.example.scenes.main.MainLayoutListener
import juniojsv.engine.example.scenes.main.MainScene
import juniojsv.engine.features.scene.Scene
import juniojsv.engine.features.utils.Debounce
import juniojsv.engine.features.utils.KeyboardHandler
import juniojsv.engine.features.utils.MovementDirection
import juniojsv.engine.features.utils.Resources
import juniojsv.engine.features.window.Resolution
import juniojsv.engine.features.window.Window
import juniojsv.engine.features.render.RenderPipeline
import org.lwjgl.glfw.GLFW

class Game(resolution: Resolution) : Window(resolution), RenderPipeline.ICallbacks {
    override val title: String = "Example"

    private val keyboard = KeyboardHandler()

    private var isCameraEnabled = false
    private val movements = mutableSetOf<MovementDirection>()

    private lateinit var scene: Scene
    private lateinit var pipeline: RenderPipeline

    private val camera
        get() = context.camera.instance

    override fun onCreate() {
        Resources.registry()
        pipeline = RenderPipeline(this)
        scene = MainScene().apply {
            layout.addListener(object : MainLayoutListener {
                override fun didResolutionScaleChanged(scale: Float) {
                    pipeline.refresh()
                }
            })
        }
        onSetupKeyBoard()
    }

    override fun onRenderScene() {
        scene.render(context)
    }

    override fun onRenderOverlay() {
        context.debugger.render(context)
    }

    override fun onRender() {
        pipeline.render(this)
        keyboard.pump(context)
        camera.move(movements)
        movements.clear()
    }

    override fun onCursorOffsetEvent(x: Double, y: Double) {
        if (!isCameraEnabled) return
        camera.rotate(x.toFloat() * 2, y.toFloat() * 2f)
        GLFW.glfwSetCursorPos(
            id,
            (resolution.width / 2).toDouble(),
            (resolution.height / 2).toDouble()
        )
    }

    override fun onMouseButtonEvent(button: Int, action: Int, mods: Int) {
        if (ImGui.getIO().wantCaptureMouse) return
        if (GLFW.GLFW_MOUSE_BUTTON_1 == button && action == GLFW.GLFW_PRESS) {
            isCameraEnabled = !isCameraEnabled
            GLFW.glfwSetInputMode(
                id,
                GLFW.GLFW_CURSOR,
                if (isCameraEnabled) GLFW.GLFW_CURSOR_DISABLED else GLFW.GLFW_CURSOR_NORMAL
            )
        }
    }

    override fun onKeyBoardEvent(key: Int, code: Int, action: Int, mods: Int) {
        keyboard.handle(key, action)
    }

    override fun onResize(width: Int, height: Int) {
        super.onResize(width, height)
        pipeline.refresh()
    }

    private fun onSetupKeyBoard() {
        val closeWindow = Debounce {
            GLFW.glfwSetWindowShouldClose(id, true)
        }
        val nextCamera = Debounce {
            if (isCameraEnabled)
                context.camera.next()
        }
        with(keyboard) {
            setKeyAction(GLFW.GLFW_KEY_ESCAPE) {
                closeWindow.invoke()
            }
            setKeyAction(GLFW.GLFW_KEY_W) {
                if (isCameraEnabled)
                    movements.add(MovementDirection.FORWARD)
            }
            setKeyAction(GLFW.GLFW_KEY_A) {
                if (isCameraEnabled)
                    movements.add(MovementDirection.LEFT)
            }
            setKeyAction(GLFW.GLFW_KEY_S) {
                if (isCameraEnabled)
                    movements.add(MovementDirection.BACKWARD)
            }
            setKeyAction(GLFW.GLFW_KEY_D) {
                if (isCameraEnabled)
                    movements.add(MovementDirection.RIGHT)
            }
            setKeyAction(GLFW.GLFW_KEY_SPACE) {
                if (isCameraEnabled)
                    movements.add(MovementDirection.UP)
            }
            setKeyAction(GLFW.GLFW_KEY_LEFT_SHIFT) {
                if (isCameraEnabled)
                    movements.add(MovementDirection.DOWN)
            }
            setKeyAction(GLFW.GLFW_KEY_C) {
                nextCamera.invoke()
            }
        }
    }
}