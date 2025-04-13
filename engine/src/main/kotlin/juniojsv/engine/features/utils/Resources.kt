package juniojsv.engine.features.utils

import juniojsv.engine.features.shader.FragmentShader
import juniojsv.engine.features.shader.Shaders
import juniojsv.engine.features.shader.VertexShader
import juniojsv.engine.features.utils.factories.ShadersProgramFactory
import juniojsv.engine.features.utils.factories.TextureFactory
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
import java.net.URL

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
    private const val CONFIG_FILE_NAME = "engine_resources.json"

    private val logger = LoggerFactory.getLogger(Resources::class.java)

    fun getConfigs(): List<Pair<URL, ResourcesConfig>> {
        logger.info("Searching for '${CONFIG_FILE_NAME}'...")
        val resourcesUrl = Thread.currentThread().contextClassLoader.getResources(CONFIG_FILE_NAME)
        if (!resourcesUrl.hasMoreElements()) {
            logger.error("Can't find the file '$CONFIG_FILE_NAME' in resources.")
            return emptyList()
        }

        val configs = mutableListOf<Pair<URL, ResourcesConfig>>()

        while (resourcesUrl.hasMoreElements()) {
            val url = resourcesUrl.nextElement()
            logger.info("Found '{}' in '{}'", CONFIG_FILE_NAME, url)
            val config = url.openStream().use { inputStream ->
                json.decodeFromString<ResourcesConfig>(inputStream.readBytes().decodeToString())
            }
            configs.add(url to config)
        }

        return configs
    }

    fun registry() {
        try {
            val configs = getConfigs()
            configs.forEach { (url, config) ->
                logger.info("Loading resources from '{}'", url)
                config.shaders?.forEach { (name, files) ->
                    ShadersProgramFactory.registry(
                        name,
                        Shaders(
                            VertexShader(files.vertex),
                            FragmentShader(files.fragment)
                        )
                    )
                }
                config.textures?.forEach { (name, value) ->
                    when (value) {
                        is Files.Single -> TextureFactory.registry(name, value.file)
                        is Files.Multiple -> TextureFactory.registry(name, value.files)
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("Error reading '$CONFIG_FILE_NAME': ${e.message}")
        } finally {
            logger.info("Resources registry finished.")
        }
    }

    fun get(file: String): InputStream {
        val stream = Resources::class.java.classLoader.getResourceAsStream(file)

        return stream ?: throw Exception("Can't find the resource $file")
    }

    fun text(file: String): String {
        val reader = get(file).bufferedReader()
        val text = reader.readText()
        reader.close()
        return text
    }
}