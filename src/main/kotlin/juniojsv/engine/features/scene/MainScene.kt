package juniojsv.engine.features.scene

import juniojsv.engine.features.entity.*
import juniojsv.engine.features.mesh.CubeMapMesh
import juniojsv.engine.features.mesh.SphereMesh
import juniojsv.engine.features.shader.DefaultInstancedShaderProgram
import juniojsv.engine.features.shader.SkyboxShaderProgram
import juniojsv.engine.features.texture.SkyboxTexture
import juniojsv.engine.features.texture.TwoDimensionTexture
import juniojsv.engine.features.ui.MainLayout
import juniojsv.engine.features.ui.MainLayoutListener
import juniojsv.engine.features.utils.SphereBoundary
import juniojsv.engine.features.window.IRenderContext
import org.joml.Vector3f
import kotlin.random.Random

class MainScene : IScene, MainLayoutListener {
    private var sky: IRender? = null
    private var spheres: MultiBeing? = null
    private val textures = mutableListOf<TwoDimensionTexture>()
    private val ui = MainLayout()

    private fun getTexturePath(index: Int) =
        "textures/metal/Metal_#-256x256.png"
            .replace("#", "$index".padStart(2, '0'))

    override fun setup(context: IRenderContext) {
        context.setCurrentAmbientLight(Light(Vector3f(-1000000f, 60000f, 0f)))
        ui.setup(context)
        ui.addListener(this)
        context.setCurrentUi(ui)
        sky = SkyBox(
            CubeMapMesh,
            SkyboxTexture,
            SkyboxShaderProgram,
            5000000f
        )
        for (i in 1 until 16) {
            val file = getTexturePath(Random.nextInt(1,21))
            textures.add(TwoDimensionTexture(file))
        }
        textures.add(TwoDimensionTexture("textures/uv.jpeg"))
    }

    override fun render(context: IRenderContext) {
        sky?.render(context)
        spheres?.render(context)
    }

    override fun onGenerateObjects(count: Int) {
        val random = Random(System.currentTimeMillis())
        val offset = 1000000

        val mesh = SphereMesh.get()
        val beings = mutableListOf<BaseBeing>()
        repeat(count) {
            beings.add(
                BaseBeing(
                    textures.random(),
                    Vector3f(
                        (random.nextInt(offset) - offset / 2).toFloat(),
                        (random.nextInt(offset / 2)).toFloat(),
                        (random.nextInt(offset) - offset / 2).toFloat()
                    ),
                    boundary = SphereBoundary(1f),
                    scale = random.nextInt(10000).toFloat()
                )
            )
        }
        spheres = MultiBeing(
            mesh,
            DefaultInstancedShaderProgram,
            beings
        )
    }
}