package juniojsv.engine.extensions

import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.nio.IntBuffer

fun FloatArray.toBuffer(): FloatBuffer {
    val buffer = MemoryUtil.memAllocFloat(size)
    buffer.put(this)
    buffer.flip()
    return buffer
}

fun IntArray.toBuffer(): IntBuffer {
    val buffer = MemoryUtil.memAllocInt(size)
    buffer.put(this)
    buffer.flip()
    return buffer
}