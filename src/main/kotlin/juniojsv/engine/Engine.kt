package juniojsv.engine

import juniojsv.engine.features.entity.*
import juniojsv.engine.features.mesh.ObjMeshProvider
import juniojsv.engine.features.utils.KeyboardHandler
import juniojsv.engine.features.utils.Mesh
import juniojsv.engine.features.utils.Shaders
import juniojsv.engine.features.utils.Textures
import juniojsv.engine.features.window.IRenderContext
import juniojsv.engine.features.window.Resolution
import juniojsv.engine.features.window.Window
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11

class Engine(resolution: Resolution) : Window(resolution) {
    override val title: String = "juniojsv.engine"

    private val keyboard = KeyboardHandler()
    private var sky: IRender? = null
    private var terrain: IRender? = null
    private var light: Being? = null

    override fun onCreate(context: IRenderContext) {
        onSetupKeyBoard(context)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glCullFace(GL11.GL_BACK)
        GL11.glClearColor(0f, 0f, 0f, 1f)
        Mesh.CUBEMAP_SHELL.decode { mesh ->
            sky = SkyBox(mesh, Textures.SKYBOX_DEFAULT_CUBEMAP, Shaders.SKYBOX_PROGRAM, 500000f)
        }
        ObjMeshProvider("mesh/terrain.obj").decode { mesh ->
            terrain = Being(
                mesh,
                Textures.TERRAIN_TEXTURE,
                Shaders.TERRAIN_PROGRAM,
                Vector3f(0f, -150000f, 0f),
                scale = 1000f
            )
        }
        context.setCurrentAmbientLight(
            Light(
                Vector3f(0f, 25850f, 540000f),
                Vector3f(.85f, .35f, .1f)
            )
        )
        ObjMeshProvider("mesh/sphere.obj").decode { mesh ->
            light = Being(
                mesh,
                null,
                Shaders.LIGHT_SHADER,
                context.getAmbientLight()!!.position,
                scale = 85000f
            )
        }
    }

    override fun draw(context: IRenderContext) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)

        sky?.render(context)
        light?.render(context)
        terrain?.render(context)

        keyboard.pump(context)
    }

    override fun onCursorOffsetEvent(context: IRenderContext, x: Double, y: Double) {
        val delta = context.getDelta().toFloat()
        context.getCamera().rotation.add(
            (x.toFloat() * 10f) * delta,
            (y.toFloat() * 10f) * delta,
            0f
        )
        GLFW.glfwSetCursorPos(
            getWindowContext().id,
            (getResolution().width / 2).toDouble(),
            (getResolution().height / 2).toDouble()
        )
    }

    override fun onMouseButtonEvent(button: Int, action: Int, mods: Int) {}

    override fun onKeyBoardEvent(context: IRenderContext, key: Int, code: Int, action: Int, mods: Int) =
        keyboard.handle(key, action)

    private fun onSetupKeyBoard(context: IRenderContext) = context.getCamera().also { camera ->
        val speed = 10000f
        with(keyboard) {
            setKeyAction(GLFW.GLFW_KEY_ESCAPE) {
                GLFW.glfwSetWindowShouldClose(
                    getWindowContext().id,
                    true
                )
            }
            setKeyAction(GLFW.GLFW_KEY_W) { delta ->
                camera.move(
                    Camera.CameraMovement.FORWARD,
                    speed * delta.toFloat()
                )
            }
            setKeyAction(GLFW.GLFW_KEY_A) { delta ->
                camera.move(
                    Camera.CameraMovement.LEFT,
                    speed * delta.toFloat()
                )
            }
            setKeyAction(GLFW.GLFW_KEY_S) { delta ->
                camera.move(
                    Camera.CameraMovement.BACKWARD,
                    speed * delta.toFloat()
                )
            }
            setKeyAction(GLFW.GLFW_KEY_D) { delta ->
                camera.move(
                    Camera.CameraMovement.RIGHT,
                    speed * delta.toFloat()
                )
            }
            setKeyAction(GLFW.GLFW_KEY_Q) { delta ->
                camera.move(
                    Camera.CameraMovement.UP,
                    speed * delta.toFloat()
                )
            }
            setKeyAction(GLFW.GLFW_KEY_E) { delta ->
                camera.move(
                    Camera.CameraMovement.DOWN,
                    speed * delta.toFloat()
                )
            }
        }
    }
}