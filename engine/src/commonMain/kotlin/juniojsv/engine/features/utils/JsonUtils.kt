package juniojsv.engine.features.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

private val module = SerializersModule {}

val json = Json { serializersModule = module }