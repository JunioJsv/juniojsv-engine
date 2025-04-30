package juniojsv.engine.platforms

actual object PlatformResources : JvmPlatformResources()

open class JvmPlatformResources : IPlatformResources {
    override fun getAll(file: String): List<Resource> {
        val urls = javaClass.classLoader.getResources(file)
        return urls.asSequence().map { Resource(it.file, it.openStream()) }.toList()
    }

    override fun get(file: String): Resource {
        return Resource(file, javaClass.classLoader.getResourceAsStream(file)!!)
    }
}