package juniojsv.engine.platforms

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
actual object PlatformResources : AndroidPlatformResources()

open class AndroidPlatformResources : IPlatformResources {
    lateinit var context: Context

    fun init(context: Context) {
        this.context = context.applicationContext
    }

    override fun getAll(file: String): List<Resource> {
        return listOf(get(file))
    }

    override fun get(file: String): Resource {
        val stream = context.assets.open(file)
        return Resource(file, stream)
    }
}
