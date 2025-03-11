package juniojsv.engine

import imgui.ImGui
import juniojsv.engine.features.entity.Camera
import juniojsv.engine.features.scene.MainScene
import juniojsv.engine.features.utils.KeyboardHandler
import juniojsv.engine.features.utils.Scale
import juniojsv.engine.features.window.IRenderContext
import juniojsv.engine.features.window.Resolution
import juniojsv.engine.features.window.Window
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11

class Engine(resolution: Resolution) : Window(resolution) {
    override val title: String = "juniojsv.engine"

    private val keyboard = KeyboardHandler()

    private var isCameraEnabled = false

    private val scene = MainScene()

    override fun onCreate(context: IRenderContext) {
        scene.setup(context)
        onSetupKeyBoard(context)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glCullFace(GL11.GL_BACK)
        GL11.glClearColor(0f, 0f, 0f, 1f)
    }

    override fun onRender(context: IRenderContext) {
        scene.render(context)
        keyboard.pump(context)
    }

    override fun onCursorOffsetEvent(context: IRenderContext, x: Double, y: Double) {
        if (!isCameraEnabled) return
        val delta = context.getDelta().toFloat()
        val speed = Scale.METER.length(10f)
        context.getCamera().rotation.add(
            (x.toFloat() * speed) * delta,
            (y.toFloat() * speed) * delta,
            0f
        )
        GLFW.glfwSetCursorPos(
            getWindowContext().id,
            (getResolution().width / 2).toDouble(),
            (getResolution().height / 2).toDouble()
        )
    }

    override fun onMouseButtonEvent(button: Int, action: Int, mods: Int) {
        if (ImGui.getIO().wantCaptureMouse) return
        if (GLFW.GLFW_MOUSE_BUTTON_1 == button && action == GLFW.GLFW_PRESS) {
            isCameraEnabled = !isCameraEnabled
            GLFW.glfwSetInputMode(
                getWindowContext().id,
                GLFW.GLFW_CURSOR,
                if (isCameraEnabled) GLFW.GLFW_CURSOR_DISABLED else GLFW.GLFW_CURSOR_NORMAL
            )
        }
    }

    override fun onKeyBoardEvent(context: IRenderContext, key: Int, code: Int, action: Int, mods: Int) {
        keyboard.handle(key, action)
    }

    private fun onSetupKeyBoard(context: IRenderContext) = context.getCamera().also { camera ->
        val speed = Scale.METER.length(100f)
        with(keyboard) {
            setKeyAction(GLFW.GLFW_KEY_ESCAPE) {
                GLFW.glfwSetWindowShouldClose(
                    getWindowContext().id,
                    true
                )
            }
            setKeyAction(GLFW.GLFW_KEY_W) { delta ->
                if (isCameraEnabled)
                    camera.move(
                        Camera.CameraMovement.FORWARD,
                        speed * delta.toFloat()
                    )
            }
            setKeyAction(GLFW.GLFW_KEY_A) { delta ->
                if (isCameraEnabled)
                    camera.move(
                        Camera.CameraMovement.LEFT,
                        speed * delta.toFloat()
                    )
            }
            setKeyAction(GLFW.GLFW_KEY_S) { delta ->
                if (isCameraEnabled)
                    camera.move(
                        Camera.CameraMovement.BACKWARD,
                        speed * delta.toFloat()
                    )
            }
            setKeyAction(GLFW.GLFW_KEY_D) { delta ->
                if (isCameraEnabled)
                    camera.move(
                        Camera.CameraMovement.RIGHT,
                        speed * delta.toFloat()
                    )
            }
            setKeyAction(GLFW.GLFW_KEY_SPACE) { delta ->
                if (isCameraEnabled)
                    camera.move(
                        Camera.CameraMovement.UP,
                        speed * delta.toFloat()
                    )
            }
            setKeyAction(GLFW.GLFW_KEY_LEFT_SHIFT) { delta ->
                if (isCameraEnabled)
                    camera.move(
                        Camera.CameraMovement.DOWN,
                        speed * delta.toFloat()
                    )
            }
        }
    }
}