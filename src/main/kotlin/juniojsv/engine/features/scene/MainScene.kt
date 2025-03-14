package juniojsv.engine.features.scene

import juniojsv.engine.features.context.WindowContext
import juniojsv.engine.features.entity.*
import juniojsv.engine.features.gui.MainLayout
import juniojsv.engine.features.gui.MainLayoutListener
import juniojsv.engine.features.texture.FileTexture
import juniojsv.engine.features.utils.Scale
import juniojsv.engine.features.utils.SphereBoundary
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
            -Scale.KILOMETER.length(100f),
            Scale.KILOMETER.length(20.7f),
            0f
        )
    )
    private val objects = mutableListOf<IRender>()
    private lateinit var floor: IRender
    private val defaultInstancedShadersProgram = ShaderProgramFactory.create(ShaderPrograms.DEFAULT_INSTANCED)
    private val defaultShaderProgram = ShaderProgramFactory.create(ShaderPrograms.DEFAULT)
    private val sphereMesh = SphereMesh(.5f).create()

    override fun setup(context: WindowContext) {
        context.render.state.ambientLight = light
        layout.setup(context)
        layout.addListener(this)
        context.gui.layout = layout
        sky = SkyBox(
            CubeMesh.create(),
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
            QuadMesh.create(),
            ShaderProgramFactory.create(ShaderPrograms.DEFAULT),
            BaseBeing(
                textures.random(),
                Vector3f(0f, -Scale.METER.length(5f), 0f),
                scale = Scale.KILOMETER.length(10f),
                rotation = Vector3f(-90f, 0f, 0f),
                textureScale = 20f
            )
        )
    }

    override fun render(context: WindowContext) {
        sky?.render(context)
        floor.render(context)
        objects.forEach { it.render(context) }
    }

    override fun onGenerateObjects(count: Int, instanced: Boolean) {
        if (count < 1) return
        objects.forEach { it.dispose() }
        objects.clear()
        val random = Random(System.currentTimeMillis())
        val offset = Scale.KILOMETER.length(10f).roundToInt()
        val maxSize = Scale.METER.length(70f)

        val beings = mutableListOf<BaseBeing>()
        val boundary = SphereBoundary(1f)
        repeat(count) {
            beings.add(
                BaseBeing(
                    textures.random(),
                    Vector3f(
                        (random.nextInt(offset) - offset / 2).toFloat(),
                        (random.nextInt(offset) + maxSize),
                        (random.nextInt(offset) - offset / 2).toFloat()
                    ),
                    boundary = boundary,
                    scale = maxSize * random.nextFloat().coerceAtLeast(0.1f),
                    textureScale = 4f * random.nextFloat().coerceAtLeast(0.1f)
                )
            )
        }

        if (instanced)
            objects.add(
                MultiBeing(
                    sphereMesh,
                    defaultInstancedShadersProgram,
                    beings
                )
            )
        else
            objects.addAll(beings.map { SingleBeing(sphereMesh, defaultShaderProgram, it) })

    }
}
