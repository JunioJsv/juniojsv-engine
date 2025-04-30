package juniojsv.engine.features.utils

import juniojsv.engine.features.shader.FragmentShader
import juniojsv.engine.features.shader.Shaders
import juniojsv.engine.features.shader.VertexShader
import juniojsv.engine.features.textures.RawTexture
import juniojsv.engine.features.utils.factories.ShadersProgramFactory
import juniojsv.engine.features.utils.factories.TextureFactory
import juniojsv.engine.platforms.PlatformDecoders
import juniojsv.engine.platforms.PlatformResources
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import org.slf4j.LoggerFactory
import java.io.InputStream

@Serializable
data class ShadersFiles(
    val vertex: String,
    val fragment: String
)

object FilesSerializer : KSerializer<Files> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Files")

    override fun deserialize(decoder: Decoder): Files {
        val input = decoder as? JsonDecoder ?: error("Only works with JSON")
        val element = input.decodeJsonElement()

        return when {
            element is JsonPrimitive && element.isString -> {
                Files.Single(element.content)
            }

            element is JsonArray -> {
                val list = Array(element.size) { index ->
                    val it = element[index]
                    if (it is JsonPrimitive && it.isString) it.content
                    else error("Expected string in array")
                }
                Files.Multiple(list)
            }

            else -> error("Invalid type for Files")
        }
    }

    override fun serialize(encoder: Encoder, value: Files) {
        val output = encoder as? JsonEncoder ?: error("Only works with JSON")

        val element = when (value) {
            is Files.Single -> JsonPrimitive(value.file)
            is Files.Multiple -> JsonArray(value.files.map { JsonPrimitive(it) })
        }

        output.encodeJsonElement(element)
    }
}

@Serializable(with = FilesSerializer::class)
sealed class Files {
    data class Single(val file: String) : Files()
    data class Multiple(val files: Array<String>) : Files()
}

@Serializable
data class ResourcesConfig(
    val shaders: Map<String, ShadersFiles>? = null,
    val textures: Map<String, Files>? = null
)

object Resources {
    private val logger = LoggerFactory.getLogger(Resources::class.java)
    private val config = ResourcesConfig(
        shaders = mapOf(
            "DEFAULT" to ShadersFiles(
                vertex = "shaders/default/default.vert",
                fragment = "shaders/default/default.frag"
            ),
            "DEFAULT_INSTANCED" to ShadersFiles(
                vertex = "shaders/default/default_instanced.vert",
                fragment = "shaders/default/default_instanced.frag"
            ),
            "DEFAULT_INSTANCED_DEBUG" to ShadersFiles(
                vertex = "shaders/default/default_instanced.vert",
                fragment = "shaders/default/default_instanced_debug.frag"
            ),
            "WINDOW" to ShadersFiles(
                vertex = "shaders/window.vert",
                fragment = "shaders/window.frag"
            ),
            "SKYBOX" to ShadersFiles(
                vertex = "shaders/skybox.vert",
                fragment = "shaders/skybox.frag"
            ),
            "VELOCITY" to ShadersFiles(
                vertex = "shaders/velocity.vert",
                fragment = "shaders/velocity.frag"
            ),
            "VELOCITY_INSTANCED" to ShadersFiles(
                vertex = "shaders/velocity_instanced.vert",
                fragment = "shaders/velocity.frag"
            ),
            "DEBUGGER" to ShadersFiles(
                vertex = "shaders/debugger.vert",
                fragment = "shaders/debugger.frag"
            ),
            "SHADOW_MAP" to ShadersFiles(
                vertex = "shaders/shadow_map.vert",
                fragment = "shaders/shadow_map.frag"
            ),
            "SHADOW_MAP_INSTANCED" to ShadersFiles(
                vertex = "shaders/shadow_map_instanced.vert",
                fragment = "shaders/shadow_map.frag"
            )
        ),
        textures = null
    )

    fun init(): Resources {
        logger.info("Initializing resources.")
        registry(config)
        logger.info("Resources initialization finished.")
        return this
    }

    fun registry(config: ResourcesConfig) {
        try {
            config.shaders?.forEach { (name, files) ->
                logger.info("Registering ShaderProgram $name.")
                ShadersProgramFactory.registry(
                    name,
                    Shaders(
                        VertexShader(files.vertex),
                        FragmentShader(files.fragment)
                    )
                )
            }
            config.textures?.forEach { (name, value) ->
                logger.info("Registering Texture $name.")
                when (value) {
                    is Files.Single -> TextureFactory.registry(name, value.file)
                    is Files.Multiple -> TextureFactory.registry(name, value.files)
                }
            }
        } catch (e: Exception) {
            logger.error("Failed to registry resources.", e)
        } finally {
            logger.info("Resources registry finished.")
        }
    }

    fun get(file: String): InputStream {
        return PlatformResources.get(file).stream
    }

    fun text(file: String): String {
        val reader = get(file).bufferedReader()
        val text = reader.readText()
        reader.close()
        return text
    }

    fun texture(file: String): RawTexture {
        return PlatformDecoders.texture(get(file))
    }
}