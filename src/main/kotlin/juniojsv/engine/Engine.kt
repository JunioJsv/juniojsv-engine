package juniojsv.engine

import imgui.ImGui
import juniojsv.engine.features.context.WindowContext
import juniojsv.engine.features.entity.BaseBeing
import juniojsv.engine.features.entity.Camera
import juniojsv.engine.features.entity.SingleBeing
import juniojsv.engine.features.entity.debugger.Debugger
import juniojsv.engine.features.gui.MainLayoutListener
import juniojsv.engine.features.scene.IScene
import juniojsv.engine.features.scene.MainScene
import juniojsv.engine.features.utils.FrameBuffer
import juniojsv.engine.features.utils.KeyboardHandler
import juniojsv.engine.features.utils.factories.QuadMesh
import juniojsv.engine.features.utils.factories.ShaderProgramFactory
import juniojsv.engine.features.utils.factories.ShaderPrograms
import juniojsv.engine.features.window.Resolution
import juniojsv.engine.features.window.Window
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11

class Engine(resolution: Resolution) : Window(resolution) {
    override val title: String = "juniojsv.engine"

    private val keyboard = KeyboardHandler()

    private lateinit var camera: Camera
    private var isCameraEnabled = false
    private val movements = mutableSetOf<Camera.CameraMovement>()

    private lateinit var scene: IScene
    private lateinit var fbo: FrameBuffer
    private lateinit var screen: SingleBeing
    private lateinit var debugger: Debugger

    override fun onCreate(context: WindowContext) {
        onSetupScreen(context)
        scene = MainScene().apply {
            layout.addListener(object : MainLayoutListener {
                override fun onChangeResolutionScale(scale: Float) {
                    fbo.resize(getResolution().withResolutionScale(scale))
                }
            })
        }
        scene.setup(context)
        onSetupKeyBoard(context)
        camera = context.camera.instance
        debugger = Debugger()
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glCullFace(GL11.GL_BACK)
        GL11.glClearColor(0f, 0f, 0f, 1f)
    }

    override fun onRender(context: WindowContext) {
        fbo.bind()
        scene.render(context)
        if (Flags.debug)
            debugger.render(context)
        fbo.unbind()

        screen.render(context)

        keyboard.pump(context)
        camera.move(movements, context.time.deltaInSeconds)
        movements.clear()
    }

    override fun onCursorOffsetEvent(context: WindowContext, x: Double, y: Double) {
        if (!isCameraEnabled) return
        camera.rotate(x.toFloat() * 2, y.toFloat() * 2f)
        GLFW.glfwSetCursorPos(
            id,
            (getResolution().width / 2).toDouble(),
            (getResolution().height / 2).toDouble()
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

    override fun onKeyBoardEvent(context: WindowContext, key: Int, code: Int, action: Int, mods: Int) {
        keyboard.handle(key, action)
    }

    override fun onResize(context: WindowContext, width: Int, height: Int) {
        super.onResize(context, width, height)
        val resolutionScale = context.render.resolutionScale
        fbo.resize(getResolution().withResolutionScale(resolutionScale))
    }

    private fun onSetupScreen(context: WindowContext) {
        val resolutionScale = context.render.resolutionScale
        fbo = FrameBuffer(
            this,
            getResolution().withResolutionScale(resolutionScale),
            depth = true,
            color = true
        )
        screen = SingleBeing(
            QuadMesh.create(),
            ShaderProgramFactory.create(ShaderPrograms.SCREEN),
            BaseBeing(fbo.colorTexture),
            isFrustumCullingEnabled = false,
            isDebuggable = false
        )
    }

    private fun onSetupKeyBoard(context: WindowContext) {
        with(keyboard) {
            setKeyAction(GLFW.GLFW_KEY_ESCAPE) {
                GLFW.glfwSetWindowShouldClose(id, true)
            }
            setKeyAction(GLFW.GLFW_KEY_W) {
                if (isCameraEnabled)
                    movements.add(Camera.CameraMovement.FORWARD)
            }
            setKeyAction(GLFW.GLFW_KEY_A) {
                if (isCameraEnabled)
                    movements.add(Camera.CameraMovement.LEFT)
            }
            setKeyAction(GLFW.GLFW_KEY_S) {
                if (isCameraEnabled)
                    movements.add(Camera.CameraMovement.BACKWARD)
            }
            setKeyAction(GLFW.GLFW_KEY_D) {
                if (isCameraEnabled)
                    movements.add(Camera.CameraMovement.RIGHT)
            }
            setKeyAction(GLFW.GLFW_KEY_SPACE) {
                if (isCameraEnabled)
                    movements.add(Camera.CameraMovement.UP)
            }
            setKeyAction(GLFW.GLFW_KEY_LEFT_SHIFT) {
                if (isCameraEnabled)
                    movements.add(Camera.CameraMovement.DOWN)
            }
        }
    }
}