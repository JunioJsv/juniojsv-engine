package juniojsv.engine.features.scene

import juniojsv.engine.features.entity.Being
import juniojsv.engine.features.entity.IRender
import juniojsv.engine.features.entity.Light
import juniojsv.engine.features.entity.SkyBox
import juniojsv.engine.features.mesh.ObjMeshProvider
import juniojsv.engine.features.ui.MainLayoutListener
import juniojsv.engine.features.utils.Mesh
import juniojsv.engine.features.utils.Shaders
import juniojsv.engine.features.utils.Textures
import juniojsv.engine.features.window.IRenderContext
import org.joml.Vector3f
import kotlin.random.Random

class MainScene : IScene, MainLayoutListener {
    private var sky: IRender? = null
    private var terrain: IRender? = null
    private val spheres = mutableListOf<Being>()

    override fun setup(context: IRenderContext) {
        context.setCurrentAmbientLight(Light(Vector3f(0f)))
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
    }

    override fun render(context: IRenderContext) {
        sky?.render(context)
        terrain?.render(context)
        spheres.forEach { sphere ->
            sphere.render(context)
        }
    }

    override fun onGenerateObjects(count: Int) {
        val random = Random(System.currentTimeMillis())
        spheres.clear()
        val offset = 1000000
        ObjMeshProvider("mesh/sphere.obj").decode { mesh ->
            repeat(count) {
                spheres.add(
                    Being(
                        mesh,
                        Textures.TEST_TEXTURE,
                        Shaders.DEFAULT_PROGRAM,
                        Vector3f(
                            (random.nextInt(offset) - offset / 2).toFloat(),
                            (random.nextInt(offset / 2)).toFloat(),
                            (random.nextInt(offset) - offset / 2).toFloat()
                        ),
                        scale = random.nextInt(10000).toFloat()
                    )
                )
            }
        }
    }
}