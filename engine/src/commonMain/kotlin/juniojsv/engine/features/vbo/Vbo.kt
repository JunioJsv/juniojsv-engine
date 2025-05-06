package juniojsv.engine.features.vbo

import juniojsv.engine.features.utils.IDisposable
import juniojsv.engine.platforms.GL
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class Vbo : IDisposable {
    companion object {
        @JvmStatic
        protected val logger: Logger = LoggerFactory.getLogger(Vbo::class.java)
    }

    abstract val vao: Int
    abstract val location: Int
    abstract val divisor: Int

    open var id: Int = GL.glGenBuffers()
        protected set

    override fun dispose() {
        super.dispose()
        GL.glDeleteBuffers(id)
    }
}