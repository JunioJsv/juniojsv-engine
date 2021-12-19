package juniojsv.engine

import juniojsv.engine.features.entity.*
import juniojsv.engine.features.mesh.ObjMeshProvider
import juniojsv.engine.features.utils.Mesh
import juniojsv.engine.features.utils.Shaders
import juniojsv.engine.features.utils.Textures
import juniojsv.engine.features.window.IRenderContext
import juniojsv.engine.features.window.Resolution
import juniojsv.engine.features.window.Window
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Engine(resolution: Resolution) : Window(resolution) {
    private lateinit var light: Light
    private lateinit var drawables: MutableList<IRender>
    private lateinit var camera: Camera
    private lateinit var sum: IRender

    override val title: String = "juniojsv.engine"

    override fun onCreate() {

        camera = Camera(Vector3f(0f), this)
        drawables = mutableListOf()
        light = Light(Vector3f(0f, 1000f, 0f))

        val scale = 2f

        Mesh.CUBEMAP_SHELL.decode { mesh ->
            drawables.add(SkyBox(mesh, Textures.SKYBOX_DEFAULT_CUBEMAP, Shaders.SKYBOX_EFFECT, 500f * scale))
        }

        ObjMeshProvider("mesh/terrain.obj").decode { mesh ->
            drawables.add(
                Being(
                    mesh,
                    null,
                    Shaders.DEFAULT_EFFECT,
                    Vector3f(0f, -150f * scale, 0f),
                    scale = scale
                )
            )
        }

        ObjMeshProvider("mesh/sphere.obj").decode { mesh ->
            sum = Being(
                mesh,
                null,
                Shaders.DEPTH_EFFECT,
                light.position,
                scale = 100f
            )
            repeat(18) {
                val position = Vector3f(
                    Random.nextFloat() * 500f - 250f,
                    Random.nextFloat() * 256f,
                    Random.nextFloat() * 500f - 250f
                )
                drawables.add(
                    Being(
                        mesh,
                        null,
                        Shaders.DEFAULT_EFFECT,
                        position,
                        scale = Random.nextFloat() * 5f
                    )
                )
            }
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glCullFace(GL11.GL_BACK)
        GL11.glClearColor(1f, 1f, 1f, 1f)
    }

    override fun draw(context: IRenderContext) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)

        val time = (GLFW.glfwGetTime() / 8)

        light.position.set(
            Vector3f(
                cos(time).toFloat() * 1000f,
                -sin(time).toFloat() * 1000f,
                0f
            )
        )

        for (entity in drawables) {
            entity.render(context, camera, light)
        }
        sum.render(context, camera, light)
    }

    override fun onCursorOffsetEvent(context: IRenderContext, offsetX: Double, offsetY: Double) {
        val delta = context.getDelta()
        camera.rotate((offsetX * 10f) * delta, (offsetY * 10f) * delta)
        GLFW.glfwSetCursorPos(
            getWindowContext().id,
            (getResolution().width / 2).toDouble(),
            (getResolution().height / 2).toDouble()
        )
    }

    override fun onMouseButtonEvent(button: Int, action: Int, mods: Int) {}

    override fun onKeyBoardEvent(context: IRenderContext, key: Int, code: Int, action: Int, mods: Int) {
        val speed = 500f
        val delta = context.getDelta().toFloat()
        when (key) {
            GLFW.GLFW_KEY_ESCAPE ->
                GLFW.glfwSetWindowShouldClose(getWindowContext().id, true)
            GLFW.GLFW_KEY_W -> camera.move(Camera.CameraMovement.FORWARD, speed * delta)
            GLFW.GLFW_KEY_A -> camera.move(Camera.CameraMovement.LEFT, speed * delta)
            GLFW.GLFW_KEY_S -> camera.move(Camera.CameraMovement.BACKWARD, speed * delta)
            GLFW.GLFW_KEY_D -> camera.move(Camera.CameraMovement.RIGHT, speed * delta)
            GLFW.GLFW_KEY_Q -> camera.move(Camera.CameraMovement.UP, speed * delta)
            GLFW.GLFW_KEY_E -> camera.move(Camera.CameraMovement.DOWN, speed * delta)
        }
    }
}