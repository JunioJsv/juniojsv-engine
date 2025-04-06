package juniojsv.engine.features.texture

import juniojsv.engine.features.utils.IDisposable
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryUtil
import java.io.InputStream
import java.nio.ByteOrder
import java.nio.IntBuffer
import java.util.concurrent.Executors
import javax.imageio.ImageIO

data class RawTexture(val pixels: IntBuffer, val width: Int, val height: Int) : IDisposable {
    override fun dispose() {
        super.dispose()
        MemoryUtil.memFree(pixels)
    }
}

abstract class Texture {
    var id: Int = GL11.glGenTextures()
        internal set

    abstract fun getBindType(): Int

    abstract fun getType(): Int

    fun bind() = TextureUnits.bind(0, this)

    fun dispose() {
        GL11.glDeleteTextures(id)
    }

    protected fun getRawTexture(stream: InputStream): RawTexture = Executors.newSingleThreadExecutor().run {
        submit<RawTexture> {
            ImageIO.read(stream).run {
                val buffer = IntArray(width * height)

                getRGB(0, 0, width, height, buffer, 0, width)

                val raw = IntArray(width * height) { index ->
                    val a: Int = buffer[index] and -0x1000000 shr 24
                    val r: Int = buffer[index] and 0xff0000 shr 16
                    val g: Int = buffer[index] and 0xff00 shr 8
                    val b: Int = buffer[index] and 0xff
                    a shl 24 or (b shl 16) or (g shl 8) or r
                }

                val pixels = MemoryUtil.memAlloc(raw.size shl 2)
                    .order(ByteOrder.nativeOrder())
                    .asIntBuffer()
                    .put(raw)
                    .flip()

                RawTexture(pixels, width, height)
            }
        }
    }.get()

    companion object {
        fun Set<Texture>.bind() = TextureUnits.bind(this)
    }
}