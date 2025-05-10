package juniojsv.engine.platforms

import java.nio.*

actual object PlatformMemory : AndroidPlatformMemory()

open class AndroidPlatformMemory : IPlatformMemory {
    override fun alloc(size: Int): ByteBuffer {
        return ByteBuffer.allocateDirect(size)
            .order(ByteOrder.nativeOrder())
    }

    override fun allocInt(size: Int): IntBuffer {
        return ByteBuffer
            .allocateDirect(size * 4)
            .order(ByteOrder.nativeOrder())
            .asIntBuffer()
    }

    override fun allocFloat(size: Int): FloatBuffer {
        return ByteBuffer.allocateDirect(size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
    }

    override fun free(buffer: Buffer) {}
}