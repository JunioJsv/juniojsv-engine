package juniojsv.engine.extensions

import org.lwjgl.BufferUtils
import java.nio.FloatBuffer
import java.nio.IntBuffer

fun FloatArray.toBuffer(): FloatBuffer = BufferUtils.createFloatBuffer(this.size).apply {
    put(this@toBuffer)
    flip()
}

fun IntArray.toBuffer(): IntBuffer = BufferUtils.createIntBuffer(this.size).apply {
    put(this@toBuffer)
    flip()
}