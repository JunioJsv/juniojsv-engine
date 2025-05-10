package juniojsv.engine.features.window

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.WindowManager
import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.context.WindowContext
import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.PlatformResources
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

abstract class AndroidWindow(private val applicationContext: Context) : PlatformWindow(),
    GLSurfaceView.Renderer {
    override lateinit var context: IWindowContext
    override lateinit var resolution: Resolution

    init {
        init()
    }

    override fun init() {
        PlatformResources.init(applicationContext)
        val wm = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.also {
            resolution = Resolution(it.width, it.height)
        }
        setup()
    }

    override fun isRunning(): Boolean = true

    override fun setup() {
        context = WindowContext(this)
    }

    public override fun dispose() {
        context.dispose()
    }

    final override fun onSurfaceCreated(
        gl: GL10?,
        config: EGLConfig?
    ) {
        onCreate()
    }

    final override fun onSurfaceChanged(
        gl: GL10?,
        width: Int,
        height: Int
    ) {
        GL.glViewport(0, 0, width, height)
        resolution = Resolution(width, height)
    }

    final override fun onDrawFrame(gl: GL10?) {
        (context as WindowContext).apply {
            onPreRender()
            onRender()
            onPostRender()
        }
    }
}