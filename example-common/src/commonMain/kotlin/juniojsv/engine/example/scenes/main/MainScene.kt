package juniojsv.engine.example.scenes.main

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.entities.Entity
import juniojsv.engine.features.entities.MaterialConfig
import juniojsv.engine.features.entities.PhysicsConfig
import juniojsv.engine.features.entities.Player
import juniojsv.engine.features.mesh.ObjMeshLoader
import juniojsv.engine.features.render.EntityRender
import juniojsv.engine.features.render.MultiEntityRender
import juniojsv.engine.features.render.Render
import juniojsv.engine.features.render.SkyBox
import juniojsv.engine.features.scene.Scene
import juniojsv.engine.features.textures.FileAtlasTexture
import juniojsv.engine.features.utils.BoundaryEllipsoid
import juniojsv.engine.features.utils.Scale
import juniojsv.engine.features.utils.Transform
import juniojsv.engine.features.utils.factories.*
import org.joml.AxisAngle4d
import org.joml.Quaternionf
import org.joml.Vector3f
import java.lang.Math.toRadians
import kotlin.math.roundToInt
import kotlin.random.Random

class MainScene : Scene(), MainLayoutCallbacks {
    private lateinit var skybox: SkyBox
    val layout = MainLayout(this)
    private val objects = mutableListOf<Render>()
    private lateinit var floor: Render
    private val defaultShadersProgram = ShadersProgramFactory.createDefault()
    private val defaultShaderProgramInstanced = ShadersProgramFactory.createDefaultInstanced()
    private val objMeshLoader = ObjMeshLoader()
    private val meshes = arrayOf(
        CubeMesh.create(),
        SphereMesh(.5f).create(),
        objMeshLoader.get("models/teapot.obj", BoundaryEllipsoid(Vector3f(1f, .5f, .6f))),
    )
    private val cubemaps = TextureFactory.getCubeMaps()
    private val atlas = FileAtlasTexture("textures/atlas.png", 7, 3)

    private lateinit var player: Player

    override fun setup(context: IWindowContext) {
        super.setup(context)

        floor = EntityRender(
            CubeMesh.create(),
            defaultShadersProgram,
            Entity(
                Transform(
                    Vector3f(0f, Scale.KILOMETER.length(1.8f), 0f),
                    scale = Vector3f(Scale.KILOMETER.length(0.995f)),
                    rotation = Quaternionf(
                        AxisAngle4d(
                            toRadians(7.0),
                            Vector3f(-1f, 0f, 0f).normalize()
                        )
                    )
                ),
                MaterialConfig(
                    TextureFactory.createTexture("METAL_07"),
                    scale = 500f,
                ),
                PhysicsConfig(mass = 0f)
            ).also {
                val y = Vector3f(it.transform.position).add(it.transform.scale).y
                player = Player(
                    Transform(Vector3f(0f, y + Scale.METER.length(10f), 0f))
                )
            }
        )

        context.gui.layout = layout
        context.camera.instance.position.add(Vector3f(0f, Scale.KILOMETER.length(3f), 0f))
        context.camera.setAsCurrent(player.cameraName)

        skybox = SkyBox(
            SkyboxMesh.create(),
            cubemaps.first(),
            ShadersProgramFactory.create("SKYBOX"),
            scale = Scale.KILOMETER.length(100f)
        )
        setSkyboxAsAmbientLight(context)
    }

    override fun render(context: IWindowContext) {
        super.render(context)

        skybox.render(context)
        floor.render(context)
        objects.forEach { it.render(context) }
        player.render(context)
    }

    override fun onGenerateObjects(count: Int, instanced: Boolean) {
        if (count < 1) return
        objects.forEach { it.dispose() }
        objects.clear()
        val random = Random(System.currentTimeMillis())
        val offset = Scale.KILOMETER.length(.3f).roundToInt()
        val maxSize = (Scale.KILOMETER.length(1f) / count).coerceAtLeast(Scale.METER.length(2f))

        meshes.forEach { mesh ->
            val entities = List(count / meshes.size) {
                val size = maxSize * random.nextFloat().coerceAtLeast(0.5f)
                Entity(
                    Transform(
                        Vector3f(
                            (random.nextInt(offset) - offset / 2).toFloat(),
                            (random.nextInt(offset) + maxSize) + Scale.KILOMETER.length(3f),
                            (random.nextInt(offset) - offset / 2).toFloat()
                        ),
                        rotation = Quaternionf(
                            AxisAngle4d(
                                random.nextDouble() * toRadians(360.0),
                                Vector3f(random.nextFloat()).normalize()
                            )
                        ),
                        scale = Vector3f(size)
                    ),
                    MaterialConfig(
                        atlas.getCell(random.nextInt(atlas.cells)),
                        scale = 4f * random.nextFloat().coerceAtLeast(0.1f),
                    ),
                    PhysicsConfig(
                        mass = 100f * size
                    )
                )
            }

            if (instanced) {
                objects.add(
                    MultiEntityRender(mesh, defaultShaderProgramInstanced, entities)
                )
            } else {
                objects.addAll(entities.map { EntityRender(mesh, defaultShadersProgram, it) })
            }
        }
    }

    private fun setSkyboxAsAmbientLight(context: IWindowContext) {
        val light = skybox.getAmbientLight()
        context.render.ambientLight = light
    }

    fun getCurrentSkyboxIndex(): Int {
        return cubemaps.indexOf(skybox.texture)
    }

    override fun getSkyboxCount(): Int {
        return cubemaps.size
    }

    override fun setSkybox(context: IWindowContext, index: Int) {
        skybox.texture = cubemaps[index]
        setSkyboxAsAmbientLight(context)
    }
}
