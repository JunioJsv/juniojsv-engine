package juniojsv.engine.features.scene

import juniojsv.engine.features.entity.Being
import juniojsv.engine.features.entity.IRender
import juniojsv.engine.features.entity.Light
import juniojsv.engine.features.entity.SkyBox
import juniojsv.engine.features.mesh.CubeMapMesh
import juniojsv.engine.features.mesh.SphereMesh
import juniojsv.engine.features.shader.DefaultShaderProgram
import juniojsv.engine.features.shader.SkyboxShaderProgram
import juniojsv.engine.features.texture.SkyboxTexture
import juniojsv.engine.features.texture.TestTexture
import juniojsv.engine.features.ui.MainLayoutListener
import juniojsv.engine.features.utils.SphereBoundary
import juniojsv.engine.features.window.IRenderContext
import org.joml.Vector3f
import kotlin.random.Random

class MainScene : IScene, MainLayoutListener {
    private var sky: IRender? = null
    private val spheres = mutableListOf<Being>()

    override fun setup(context: IRenderContext) {
        context.setCurrentAmbientLight(Light(Vector3f(-1000000f, 60000f, 0f)))
        sky = SkyBox(
            CubeMapMesh,
            SkyboxTexture,
            SkyboxShaderProgram,
            5000000f
        )
    }

    override fun render(context: IRenderContext) {
        sky?.render(context)
        spheres.forEach { sphere ->
            sphere.render(context)
        }
    }

    override fun onGenerateObjects(count: Int) {
        val random = Random(System.currentTimeMillis())
        spheres.clear()
        val offset = 1000000

        val mesh = SphereMesh.get()
        repeat(count) {
            spheres.add(
                Being(
                    mesh,
                    TestTexture,
                    DefaultShaderProgram,
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
    }
}