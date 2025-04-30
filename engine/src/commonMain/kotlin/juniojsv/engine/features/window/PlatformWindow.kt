package juniojsv.engine.features.window

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.context.WindowContext
import juniojsv.engine.features.utils.Resources
import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.constants.GL_BACK
import juniojsv.engine.platforms.constants.GL_CULL_FACE
import juniojsv.engine.platforms.constants.GL_DEPTH_TEST
import juniojsv.engine.platforms.constants.GL_SHADING_LANGUAGE_VERSION
import juniojsv.engine.platforms.constants.GL_VERSION
import org.slf4j.LoggerFactory

abstract class PlatformWindow {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    abstract val context: IWindowContext
    abstract val resolution: Resolution

    open fun init() {
        setup()
        onCreate()
        while (isRunning()) (context as WindowContext).apply {
            try {
                onPreRender()
                onRender()
                onPostRender()
            } catch (e: Exception) {
                logger.error(e.message, e.cause)
                break
            }
        }
        dispose()
    }

    abstract fun isRunning(): Boolean
    protected abstract fun setup()
    protected open fun onCreate() {
        GL.glEnable(GL_DEPTH_TEST)
        GL.glEnable(GL_CULL_FACE)
        GL.glCullFace(GL_BACK)
        GL.glClearColor(0f, 0f, 0f, 1f)
        Resources.init()
        val glVersion = GL.glGetString(GL_VERSION)
        val glShadingLanguageVersion = GL.glGetString(GL_SHADING_LANGUAGE_VERSION)
        logger.info("OpenGL version: $glVersion")
        logger.info("OpenGL shading language version: $glShadingLanguageVersion")
    }

    protected abstract fun onRender()
    protected abstract fun dispose()
}