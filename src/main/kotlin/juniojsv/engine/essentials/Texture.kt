package juniojsv.engine.essentials

import juniojsv.engine.utils.Resource
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import javax.imageio.ImageIO

abstract class Texture : Resource() {
    val id: Int = GL11.glGenTextures()

    protected fun decode(stream: InputStream, onSuccess: (pixels: IntBuffer, width: Int, height: Int) -> Unit) {
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

    class TwoDimension(file: String) : Texture() {
        init {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id)
            getResources(file) { stream ->
                decode(stream) { pixels, width, height ->
                    GL11.glTexImage2D(
                        GL11.GL_TEXTURE_2D,
                        0, GL11.GL_RGBA,
                        width, height, 0,
                        GL11.GL_RGBA,
                        GL11.GL_UNSIGNED_BYTE,
                        pixels
                    )

                    GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
                }
            }
            GL11.glTexParameteri(
                GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT
            )
            GL11.glTexParameteri(
                GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT
            )
            GL11.glTexParameteri(
                GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST
            )
            GL11.glTexParameteri(
                GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR
            )
            GL11.glTexParameteri(
                GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST
            )
        }
    }

    class CubeMap(files: Array<String>) : Texture() {
        init {
            GL11.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, id)
            files.forEachIndexed { index, file ->
                getResources(file) { stream ->
                    decode(stream) { pixels, width, height ->
                        GL11.glTexImage2D(
                            GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + index,
                            0, GL11.GL_RGBA, width, height, 0,
                            GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels
                        )
                    }
                }
            }
            GL11.glTexParameteri(
                GL20.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR
            )
            GL11.glTexParameteri(
                GL20.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR
            )
            GL11.glTexParameteri(
                GL20.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE
            )
            GL11.glTexParameteri(
                GL20.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE
            )
            GL11.glTexParameteri(
                GL20.GL_TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_WRAP_R, GL20.GL_CLAMP_TO_EDGE
            )
            GL11.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, 0)
        }
    }
}