package juniojsv.engine.example.android

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView

class MainActivity : AppCompatActivity() {
    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var composeView: ComposeView
    lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        game = Game(applicationContext)
        glSurfaceView = findViewById<GLSurfaceView>(R.id.glSurfaceView).apply {
            debugFlags = GLSurfaceView.DEBUG_CHECK_GL_ERROR or GLSurfaceView.DEBUG_LOG_GL_CALLS
            setEGLContextClientVersion(3)
            setRenderer(game)
        }
        composeView = findViewById<ComposeView>(R.id.composeView).apply {
            setContent {
                GameVirtualGamepad(game = game)
            }
        }
    }
}