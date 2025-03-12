package juniojsv.engine.features.scene

import juniojsv.engine.features.context.WindowContext
import juniojsv.engine.features.entity.*
import juniojsv.engine.features.mesh.CubeMapMesh
import juniojsv.engine.features.mesh.SphereMesh
import juniojsv.engine.features.shader.DefaultInstancedShaderProgram
import juniojsv.engine.features.shader.DefaultShaderProgram
import juniojsv.engine.features.shader.SkyboxShaderProgram
import juniojsv.engine.features.texture.SkyboxTexture
import juniojsv.engine.features.texture.TwoDimensionTexture
import juniojsv.engine.features.ui.MainLayout
import juniojsv.engine.features.ui.MainLayoutListener
import juniojsv.engine.features.utils.Scale
import juniojsv.engine.features.utils.SphereBoundary
import org.joml.Vector3f
import kotlin.math.roundToInt
import kotlin.random.Random

class MainScene : IScene, MainLayoutListener {
    private var sky: IRender? = null
    private val textures = mutableListOf<TwoDimensionTexture>()
    private val layout = MainLayout()
    private val light = Light(
        Vector3f(
            -Scale.KILOMETER.length(100f),
            Scale.KILOMETER.length(20.7f),
            0f
        )
    )
    private val objects = mutableListOf<IRender>()

    private fun getTexturePath(index: Int) =
        "textures/metal/Metal_#-256x256.png"
            .replace("#", "$index".padStart(2, '0'))

    override fun setup(context: WindowContext) {
        context.render.setLight(light)
        layout.setup(context)
        layout.addListener(this)
        context.gui.layout = layout
        sky = SkyBox(
            CubeMapMesh,
            SkyboxTexture,
            SkyboxShaderProgram,
            Scale.KILOMETER.length(100f)
        )
        for (i in 1 until 16) {
            val file = getTexturePath(Random.nextInt(1, 21))
            textures.add(TwoDimensionTexture(file))
        }
        textures.add(TwoDimensionTexture("textures/uv.jpeg"))
    }

    override fun render(context: WindowContext) {
        sky?.render(context)
        objects.forEach { it.render(context) }
    }

    override fun onGenerateObjects(count: Int, instanced: Boolean) {
        if (count < 1) return
        objects.clear()
        val random = Random(System.currentTimeMillis())
        val offset = Scale.KILOMETER.length(10f).roundToInt()

        val mesh = SphereMesh.get()
        val beings = mutableListOf<BaseBeing>()
        repeat(count) {
            beings.add(
                BaseBeing(
                    textures.random(),
                    Vector3f(
                        (random.nextInt(offset) - offset / 2).toFloat(),
                        (random.nextInt(offset) - offset / 2).toFloat(),
                        (random.nextInt(offset) - offset / 2).toFloat()
                    ),
                    boundary = SphereBoundary(1f),
                    scale = Scale.METER.length(70f) * random.nextFloat().coerceAtLeast(0.1f)
                )
            )
        }

        if (instanced)
            objects.add(
                MultiBeing(
                    mesh,
                    DefaultInstancedShaderProgram,
                    beings
                )
            )
        else
            objects.addAll(beings.map { SingleBeing(mesh, DefaultShaderProgram, it) })

    }
}