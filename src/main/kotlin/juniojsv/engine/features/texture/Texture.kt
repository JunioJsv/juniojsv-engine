package juniojsv.engine.features.texture

import org.lwjgl.opengl.GL11
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import javax.imageio.ImageIO

abstract class Texture {
    val id: Int = GL11.glGenTextures()

    protected fun getRawData(stream: InputStream, onSuccess: (pixels: IntBuffer, width: Int, height: Int) -> Unit) {
        ImageIO.read(stream).apply {
            val buffer = IntArray(width * height)

            getRGB(0, 0, width, height, buffer, 0, width)

            val raw = IntArray(width * height) { index ->
                val a: Int = buffer[index] and -0x1000000 shr 24
                val r: Int = buffer[index] and 0xff0000 shr 16
                val g: Int = buffer[index] and 0xff00 shr 8
                val b: Int = buffer[index] and 0xff
                a shl 24 or (b shl 16) or (g shl 8) or r
            }

            val pixels = ByteBuffer.allocateDirect(raw.size shl 2)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .put(raw)
                .flip()

            onSuccess(pixels, width, height)
        }
    }
}