package juniojsv.engine.utils

import java.io.InputStream

abstract class Resource {
    protected fun getResources(file: String, onSuccess: (stream: InputStream) -> Unit) {
        Resource::class.java.classLoader.also { loader ->
            loader.getResourceAsStream(file)?.also { stream ->
                onSuccess(stream)
            } ?: throw Exception("Can't find the resource")
        }
    }
}