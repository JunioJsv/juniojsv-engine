package juniojsv.engine.example

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent

class MainActivity : AppCompatActivity() {
    private lateinit var glSurfaceView: GLSurfaceView
    lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        game = Game(applicationContext)
        glSurfaceView = GLSurfaceView(applicationContext).apply {
            debugFlags = GLSurfaceView.DEBUG_CHECK_GL_ERROR or GLSurfaceView.DEBUG_LOG_GL_CALLS
            setEGLContextClientVersion(3)
            setRenderer(game)
        }
        setContentView(glSurfaceView)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        game.onTouchEvent(event)
        return super.onTouchEvent(event)
    }
}