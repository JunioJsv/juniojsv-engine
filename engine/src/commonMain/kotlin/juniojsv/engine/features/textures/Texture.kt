package juniojsv.engine.features.textures

import juniojsv.engine.features.utils.IDisposable
import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.PlatformMemory
import org.joml.Vector3f
import java.nio.IntBuffer

data class PixelLuminance(val x: Int, val y: Int, val luminance: Double, val color: Vector3f)

class RawTexture(val pixels: IntBuffer, val width: Int, val height: Int) : IDisposable {
    override fun dispose() {
        super.dispose()
        PlatformMemory.free(pixels)
    }

    fun put(x: Int, y: Int, pixel: Int) {
        pixels.put(y * width + x, pixel)
    }

    fun put(x: Int, y: Int, width: Int, height: Int, pixels: IntBuffer) {
        for (i in 0 until height) {
            for (j in 0 until width) {
                put(x + j, y + i, pixels.get(i * width + j))
            }
        }
    }

    fun resized(newWidth: Int, newHeight: Int): RawTexture {
        val newPixels = PlatformMemory.allocInt(newWidth * newHeight)
        val scaleX = width.toFloat() / newWidth
        val scaleY = height.toFloat() / newHeight

        for (y in 0 until newHeight) {
            val srcY = y * scaleY
            val y0 = srcY.toInt().coerceIn(0, height - 1)

            for (x in 0 until newWidth) {
                val srcX = x * scaleX
                val x0 = srcX.toInt().coerceIn(0, width - 1)

                val srcIndex = y0 * width + x0
                val pixel = pixels.get(srcIndex)

                newPixels.put(y * newWidth + x, pixel)
            }
        }

        newPixels.rewind()
        return RawTexture(newPixels, newWidth, newHeight)
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
    open var id: Int = GL.glGenTextures()
        protected set
    abstract val width: Int
    abstract val height: Int

    abstract fun getBindType(): Int

    abstract fun getType(): Int

    fun bind() = TextureUnits.bind(0, this)

    fun dispose() {
        GL.glDeleteTextures(id)
    }

    companion object {
        fun Set<Texture>.bind() = TextureUnits.bind(this)
    }
}
