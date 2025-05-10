package juniojsv.engine.platforms

import juniojsv.engine.features.textures.RawTexture
import java.io.InputStream
import java.nio.ByteOrder
import java.nio.IntBuffer
import java.util.concurrent.Executors
import javax.imageio.ImageIO

actual object PlatformDecoders : JvmPlatformDecoders()

open class JvmPlatformDecoders : IPlatformDecoders {
    override fun texture(stream: InputStream): RawTexture = Executors.newSingleThreadExecutor().run {
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

                val pixels = PlatformMemory.alloc(raw.size shl 2)
                    .order(ByteOrder.nativeOrder())
                    .asIntBuffer()
                    .put(raw)
                    .flip()

                RawTexture(pixels as IntBuffer, width, height)
            }
        }
    }.get()
}