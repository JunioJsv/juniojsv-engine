package juniojsv.engine.extensions

import juniojsv.engine.platforms.PlatformMemory
import org.joml.Matrix4f
import java.nio.FloatBuffer
import java.nio.IntBuffer

fun FloatArray.toBuffer(): FloatBuffer {
    val buffer = PlatformMemory.allocFloat(size)
    buffer.put(this)
    buffer.flip()
    return buffer
}

fun IntArray.toBuffer(): IntBuffer {
    val buffer = PlatformMemory.allocInt(size)
    buffer.put(this)
    buffer.flip()
    return buffer
}

fun Matrix4f.toBuffer(): FloatBuffer {
    val buffer = PlatformMemory.allocFloat(16)
    get(buffer)
    return buffer
}
