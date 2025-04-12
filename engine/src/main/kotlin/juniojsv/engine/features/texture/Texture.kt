package juniojsv.engine.features.texture

import juniojsv.engine.features.utils.IDisposable
import org.joml.Vector3f
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryUtil
import java.io.InputStream
import java.nio.ByteOrder
import java.nio.IntBuffer
import java.util.concurrent.Executors
import javax.imageio.ImageIO

data class PixelLuminance(val x: Int, val y: Int, val luminance: Double, val color: Vector3f)

class RawTexture internal constructor(val pixels: IntBuffer, val width: Int, val height: Int) : IDisposable {
    override fun dispose() {
        super.dispose()
        MemoryUtil.memFree(pixels)
    }

    /**
     * Finds the pixel with the maximum luminance in the texture.
     *
     * @param steps The step size for iterating through the pixels. A smaller step size increases accuracy but also increases processing time.
     * @return A [PixelLuminance] object containing the coordinates and luminance of the brightest pixel.
     */
    fun getMaxPixelLuminance(steps: Int = 1): PixelLuminance {
        var max = 0.0
        var maxX = 0
        var maxY = 0

        val color = Vector3f()

        if (width == 0 || height == 0) {
            return PixelLuminance(0, 0, 0.0, color)
        }


        for (i in 0 until width * height step steps) {
            val pixel = pixels.get(i)
            val r = pixel and 0xFF
            val g = pixel shr 8 and 0xFF
            val b = pixel shr 16 and 0xFF

            val rf = r / 255.0
            val gf = g / 255.0
            val bf = b / 255.0

            val luminance = 0.2126 * rf + 0.7152 * gf + 0.0722 * bf
            if (luminance > max) {
                max = luminance
                maxX = i % width
                maxY = i / width
                color.set(rf.toFloat(), gf.toFloat(), bf.toFloat())
            }
        }
        return PixelLuminance(maxX, maxY, max, color)
    }
}

abstract class Texture {
    var id: Int = GL11.glGenTextures()
        protected set

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
