package juniojsv.engine.features.utils

import java.io.InputStream

class Resource {
    companion object {
        fun getResource(file: String, onSuccess: (stream: InputStream) -> Unit) {
            Resource::class.java.classLoader.also { loader ->
                loader.getResourceAsStream(file)?.also { stream ->
                    onSuccess(stream)
                } ?: throw Exception("Can't find the resource")
            }
        }
    }
}