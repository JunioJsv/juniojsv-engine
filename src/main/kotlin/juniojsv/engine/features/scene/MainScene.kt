package juniojsv.engine.features.scene

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.entity.*
import juniojsv.engine.features.gui.MainLayout
import juniojsv.engine.features.gui.MainLayoutCallbacks
import juniojsv.engine.features.texture.FileTexture
import juniojsv.engine.features.utils.Scale
import juniojsv.engine.features.utils.Transform
import juniojsv.engine.features.utils.factories.*
import org.joml.Vector3f
import kotlin.math.roundToInt
import kotlin.random.Random

class MainScene : Scene(), MainLayoutCallbacks {
    private lateinit var skybox: SkyBox
    private val textures = mutableListOf<FileTexture>()
    val layout = MainLayout(this)
    private val objects = mutableListOf<Render>()
    private lateinit var floor: Render
    private val defaultInstancedShadersProgram = ShaderProgramFactory.create(ShaderPrograms.DEFAULT_INSTANCED)
    private val defaultShaderProgram = ShaderProgramFactory.create(ShaderPrograms.DEFAULT)
    private val meshes = arrayOf(CubeMesh.create(), SphereMesh(.5f).create())
    private val skyboxes = arrayOf(
        TextureFactory.createCubeMapTexture(Textures.SUNSET_BAY_SKYBOX),
        TextureFactory.createCubeMapTexture(Textures.VERY_BIG_MOUNTAINS_SKYBOX),
        TextureFactory.createCubeMapTexture(Textures.DESERT_SKYBOX)
    )

    private val player = Player(
        Transform(Vector3f(0f, Scale.KILOMETER.length(3f), 0f))
    )

    override fun setup(context: IWindowContext) {
        super.setup(context)

        context.gui.layout = layout
        context.camera.instance.position.add(Vector3f(0f, Scale.KILOMETER.length(3f), 0f))
        context.camera.setAsCurrent(player.cameraName)

        skybox = SkyBox(
            SkyboxMesh.create(),
            skyboxes.first(),
            ShaderProgramFactory.create(ShaderPrograms.SKYBOX),
            Scale.KILOMETER.length(100f)
        )
        setSkyboxAsAmbientLight(context)

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
                ),
                TextureFactory.createTexture(Textures.METAL_07),
                textureScale = 1000f,
                mass = 0f
            )
        )
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

    private fun setSkyboxAsAmbientLight(context: IWindowContext) {
        val light = skybox.getAmbientLight()
        context.render.ambientLight = light
    }

    override fun getSkyboxCount(): Int {
        return skyboxes.size
    }

    override fun setSkybox(context: IWindowContext, index: Int) {
        skybox.texture = skyboxes[index]
        setSkyboxAsAmbientLight(context)
    }
}
