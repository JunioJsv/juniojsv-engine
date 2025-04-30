package juniojsv.engine.platforms

import java.io.InputStream

data class Resource(val file: String, val stream: InputStream)

interface IPlatformResources {
    fun getAll(file: String): List<Resource>
    fun get(file: String): Resource
}

expect object PlatformResources : IPlatformResources