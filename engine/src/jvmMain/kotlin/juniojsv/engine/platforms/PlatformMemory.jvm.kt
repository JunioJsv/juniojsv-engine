package juniojsv.engine.platforms

import org.lwjgl.system.MemoryUtil
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

actual object PlatformMemory : JvmPlatformMemory()

open class JvmPlatformMemory : IPlatformMemory {
    override fun alloc(size: Int): ByteBuffer {
        return MemoryUtil.memAlloc(size)
    }

    override fun allocInt(size: Int): IntBuffer {
        return MemoryUtil.memAllocInt(size)
    }

    override fun allocFloat(size: Int): FloatBuffer {
        return MemoryUtil.memAllocFloat(size)
    }

    override fun free(buffer: Buffer) {
        MemoryUtil.memFree(buffer)
    }
}