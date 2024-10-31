package juniojsv.engine.features.utils

import java.io.InputStream

class Resource {
    companion object {
        fun get(file: String): InputStream {
            val stream = Resource::class.java.classLoader.getResourceAsStream(file)

            return stream ?: throw Exception("Can't find the resource")
        }
    }
}