package juniojsv.engine.platforms

import android.graphics.BitmapFactory
import juniojsv.engine.features.textures.RawTexture
import java.io.InputStream
import java.nio.ByteOrder
import java.nio.IntBuffer
import java.util.concurrent.Executors

actual object PlatformDecoders : AndroidPlatformDecoders()

open class AndroidPlatformDecoders : IPlatformDecoders {
    override fun texture(stream: InputStream): RawTexture = Executors.newSingleThreadExecutor().run {
        submit<RawTexture> {
            val bitmap = BitmapFactory.decodeStream(stream)

            val width = bitmap.width
            val height = bitmap.height
            val buffer = IntArray(width * height)

            // Obtém os pixels do bitmap
            bitmap.getPixels(buffer, 0, width, 0, 0, width, height)

            // Converte os pixels para o formato desejado (RGBA para ABGR)
            val raw = IntArray(width * height) { index ->
                val pixel = buffer[index]
                val a: Int = (pixel shr 24) and 0xff
                val r: Int = (pixel shr 16) and 0xff
                val g: Int = (pixel shr 8) and 0xff
                val b: Int = pixel and 0xff
                (a shl 24) or (b shl 16) or (g shl 8) or r
            }

            val pixels = PlatformMemory.alloc(raw.size shl 2)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .put(raw)
                .flip()

            // Não se esqueça de reciclar o bitmap após o uso
            bitmap.recycle()

            RawTexture(pixels as IntBuffer, width, height)
        }
    }.get()
}