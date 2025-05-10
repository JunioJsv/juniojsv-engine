package juniojsv.engine.platforms

import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

interface IPlatformMemory {
    fun alloc(size: Int): ByteBuffer
    fun allocInt(size: Int): IntBuffer
    fun allocFloat(size: Int): FloatBuffer
    fun free(buffer: Buffer)
}

expect object PlatformMemory : IPlatformMemory