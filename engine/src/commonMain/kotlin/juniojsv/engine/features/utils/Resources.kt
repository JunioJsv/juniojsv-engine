package juniojsv.engine.features.utils

import juniojsv.engine.features.shader.FragmentShader
import juniojsv.engine.features.shader.Shaders
import juniojsv.engine.features.shader.VertexShader
import juniojsv.engine.features.textures.RawTexture
import juniojsv.engine.features.utils.ShadersConfig.Attributes.entries
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
data class ShadersConfig(
    val vertex: String,
    val fragment: String,
    val attributes: Map<Int, String> = AttributesBuilder().default().build()
) {
    enum class Attributes(val label: String, val gap: Int = 1) {
        POSITION("aPosition"),
        UV("aUV"),
        NORMAL("aNormal"),
        MODEL("aModel", 4),
        PREVIOUS_MODEL("aPreviousModel", 4),
        TEXTURE_INDEX("aTextureIndex"),
        TEXTURE_SCALE("aTextureScale");

        fun previous(): Attributes? {
            if (ordinal == 0) return null
            return entries[ordinal - 1]
        }

        fun location(): Int {
            var index = 0

            for (i in ordinal - 1 downTo 0) {
                index += entries[i].gap
            }

            return index
        }
    }

    class AttributesBuilder() {
        private val attributes = mutableMapOf<Int, String>()

        fun default(): AttributesBuilder {
            attributes.clear()
            add(Attributes.POSITION)
                .add(Attributes.UV)
                .add(Attributes.NORMAL)
            return this
        }

        fun add(attribute: Attributes): AttributesBuilder {
            attributes[attribute.location()] = attribute.label
            return this
        }

        fun addPosition(): AttributesBuilder {
            add(Attributes.POSITION)
            return this
        }

        fun addUV(): AttributesBuilder {
            add(Attributes.UV)
            return this
        }

        fun addNormal(): AttributesBuilder {
            add(Attributes.NORMAL)
            return this
        }

        fun addModel(): AttributesBuilder {
            add(Attributes.MODEL)
            return this
        }

        fun addPreviousModel(): AttributesBuilder {
            add(Attributes.PREVIOUS_MODEL)
            return this
        }

        fun addTextureIndex(): AttributesBuilder {
            add(Attributes.TEXTURE_INDEX)
            return this
        }

        fun addTextureScale(): AttributesBuilder {
            add(Attributes.TEXTURE_SCALE)
            return this
        }

        fun build(): Map<Int, String> = attributes
    }
}

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
    @Serializable
    data class Single(val file: String) : Files()

    @Serializable
    data class Multiple(val files: Array<String>) : Files()
}

@Serializable
data class ResourcesConfig(
    val shaders: Map<String, ShadersConfig>? = null,
    val textures: Map<String, Files>? = null
)

object Resources {
    private val logger = LoggerFactory.getLogger(Resources::class.java)
    private val config = ResourcesConfig(
        shaders = mapOf(
            "DEFAULT" to ShadersConfig(
                vertex = "shaders/default/default.vert",
                fragment = "shaders/default/default.frag"
            ),
            "DEFAULT_INSTANCED" to ShadersConfig(
                vertex = "shaders/default/default_instanced.vert",
                fragment = "shaders/default/default_instanced.frag",
                attributes = ShadersConfig
                    .AttributesBuilder()
                    .default()
                    .addModel()
                    .addTextureIndex()
                    .addTextureScale()
                    .build()
            ),
            "DEFAULT_INSTANCED_DEBUG" to ShadersConfig(
                vertex = "shaders/default/default_instanced.vert",
                fragment = "shaders/default/default_instanced_debug.frag",
                attributes = ShadersConfig
                    .AttributesBuilder()
                    .default()
                    .addModel()
                    .addTextureIndex()
                    .addTextureScale()
                    .build()
            ),
            "WINDOW" to ShadersConfig(
                vertex = "shaders/window.vert",
                fragment = "shaders/window.frag",
                attributes = ShadersConfig
                    .AttributesBuilder()
                    .addPosition()
                    .addUV()
                    .build()
            ),
            "SKYBOX" to ShadersConfig(
                vertex = "shaders/skybox.vert",
                fragment = "shaders/skybox.frag",
                attributes = ShadersConfig
                    .AttributesBuilder()
                    .addPosition()
                    .build()
            ),
            "VELOCITY" to ShadersConfig(
                vertex = "shaders/velocity.vert",
                fragment = "shaders/velocity.frag",
                attributes = ShadersConfig
                    .AttributesBuilder()
                    .addPosition()
                    .build()
            ),
            "VELOCITY_INSTANCED" to ShadersConfig(
                vertex = "shaders/velocity_instanced.vert",
                fragment = "shaders/velocity.frag",
                attributes = ShadersConfig
                    .AttributesBuilder()
                    .addPosition()
                    .addModel()
                    .addPreviousModel()
                    .build()
            ),
            "DEBUGGER" to ShadersConfig(
                vertex = "shaders/debugger.vert",
                fragment = "shaders/debugger.frag",
                attributes = ShadersConfig
                    .AttributesBuilder()
                    .addPosition()
                    .addModel()
                    .build()
            ),
            "SHADOW_MAP" to ShadersConfig(
                vertex = "shaders/shadow_map.vert",
                fragment = "shaders/shadow_map.frag",
                attributes = ShadersConfig
                    .AttributesBuilder()
                    .addPosition()
                    .build()
            ),
            "SHADOW_MAP_INSTANCED" to ShadersConfig(
                vertex = "shaders/shadow_map_instanced.vert",
                fragment = "shaders/shadow_map.frag",
                attributes = ShadersConfig
                    .AttributesBuilder()
                    .addPosition()
                    .addModel()
                    .build()
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
            config.shaders?.forEach { (name, config) ->
                logger.info("Registering ShaderProgram $name.")
                ShadersProgramFactory.registry(
                    name,
                    Shaders(
                        VertexShader(config.vertex),
                        FragmentShader(config.fragment),
                        config.attributes,
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