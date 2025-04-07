package juniojsv.engine.features.scene

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.entity.*
import juniojsv.engine.features.gui.MainLayout
import juniojsv.engine.features.gui.MainLayoutListener
import juniojsv.engine.features.texture.FileTexture
import juniojsv.engine.features.utils.Scale
import juniojsv.engine.features.utils.factories.*
import org.joml.Vector3f
import kotlin.math.roundToInt
import kotlin.random.Random

class MainScene : IScene, MainLayoutListener {
    private var sky: IRender? = null
    private val textures = mutableListOf<FileTexture>()
    val layout = MainLayout()
    private val light = Light(
        Vector3f(
            -Scale.KILOMETER.length(100f), Scale.KILOMETER.length(20.7f), 0f
        )
    )
    private val objects = mutableListOf<IRender>()
    private lateinit var floor: IRender
    private val defaultInstancedShadersProgram = ShaderProgramFactory.create(ShaderPrograms.DEFAULT_INSTANCED)
    private val defaultShaderProgram = ShaderProgramFactory.create(ShaderPrograms.DEFAULT)
    private val meshes = arrayOf(CubeMesh.create(), SphereMesh(.5f).create())

    override fun setup(context: IWindowContext) {
        context.camera.instance.position.add(Vector3f(0f, Scale.KILOMETER.length(3f), 0f))
        context.render.ambientLight = light
        layout.setup(context)
        layout.addListener(this)
        context.gui.layout = layout
        sky = SkyBox(
            SkyboxMesh.create(),
            TextureFactory.createCubeMapTexture(Textures.SKYBOX),
            ShaderProgramFactory.create(ShaderPrograms.SKYBOX),
            Scale.KILOMETER.length(100f)
        )
        for (i in 1 until 16) {
            val random = Random.nextInt(1, 21)
            val texture = Textures.valueOf(
                "METAL_#".replace(
                    "#", "$random".padStart(2, '0')
                )
            )
            textures.add(TextureFactory.createTexture(texture))
        }
        textures.add(TextureFactory.createTexture(Textures.TEST))
        floor = SingleBeing(
            CubeMesh.create(), ShaderProgramFactory.create(ShaderPrograms.DEFAULT), BaseBeing(
                Transform(
                    Vector3f(0f, Scale.KILOMETER.length(1.8f), 0f),
                    scale = Vector3f(Scale.KILOMETER.length(1f)),
                    rotation = Vector3f(-7f, 0f, 0f)
                ), textures.random(), textureScale = 20f, mass = 0f
            )
        )
    }

    override fun render(context: IWindowContext) {
        sky?.render(context)
        floor.render(context)
        objects.forEach { it.render(context) }
    }

    override fun onGenerateObjects(count: Int, instanced: Boolean) {
        if (count < 1) return
        objects.forEach { it.dispose() }
        objects.clear()
        val random = Random(System.currentTimeMillis())
        val offset = Scale.KILOMETER.length(.3f).roundToInt()
        val maxSize = Scale.METER.length(2f)

        meshes.forEach { mesh ->
            val beings = List(count / meshes.size) {
                val size = maxSize * random.nextFloat().coerceAtLeast(0.5f)
                BaseBeing(
                    Transform(
                        Vector3f(
                            (random.nextInt(offset) - offset / 2).toFloat(),
                            (random.nextInt(offset) + maxSize) + Scale.KILOMETER.length(3f),
                            (random.nextInt(offset) - offset / 2).toFloat()
                        ),
                        scale = Vector3f(size)
                    ),
                    textures.random(),
                    textureScale = 4f * random.nextFloat().coerceAtLeast(0.1f),
                    mass = 10f * size
                )
            }

            if (instanced) {
                objects.add(
                    MultiBeing(mesh, defaultInstancedShadersProgram, beings)
                )
            } else {
                objects.addAll(beings.map { SingleBeing(mesh, defaultShaderProgram, it) })
            }
        }
    }
}
