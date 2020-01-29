package juniojsv.engine

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL32
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import javax.imageio.ImageIO


abstract class Texture {
    abstract val id: Int

    companion object {
        fun fromRaw(pixels: IntBuffer, width: Int, height: Int): Texture {
            return object : Texture() {
                override val id = GL11.glGenTextures()

                init {
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, id)
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT)
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT)
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)

                    GL11.glTexImage2D(
                        GL11.GL_TEXTURE_2D,
                        0, GL11.GL_RGBA,
                        width, height, 0,
                        GL11.GL_RGBA,
                        GL11.GL_UNSIGNED_BYTE,
                        pixels
                    )
                    GL32.glGenerateMipmap(GL11.GL_TEXTURE_2D)
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D,0)
                }
            }
        }

        fun fromResource(fileName: String): Texture {
            var pixels: IntBuffer = BufferUtils.createIntBuffer(0)
            var width = 0
            var height = 0

            Model::class.java.classLoader.also { loader ->
                loader.getResourceAsStream(fileName)?.also { src ->
                    ImageIO.read(src).apply {
                        width = this.width
                        height = this.height

                        val buffer = IntArray(width * height)

                        getRGB(0, 0, width, height, buffer, 0, width)

                        val raw = IntArray(width * height) { index ->
                            val a: Int = buffer[index] and -0x1000000 shr 24
                            val r: Int = buffer[index] and 0xff0000 shr 16
                            val g: Int = buffer[index] and 0xff00 shr 8
                            val b: Int = buffer[index] and 0xff
                            a shl 24 or (b shl 16) or (g shl 8) or r
                        }

                        pixels = ByteBuffer.allocateDirect(raw.size shl 2)
                            .order(ByteOrder.nativeOrder())
                            .asIntBuffer()
                            .put(raw)
                            .flip()
                    }

                }
            }

            return fromRaw(pixels, width, height)
        }
    }
}