package juniojsv.engine

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import javax.imageio.ImageIO

abstract class CubeMap {
    abstract val id: Int

    companion object {
        fun fromRaw(faces: Array<IntBuffer>, width: Int, height: Int): CubeMap {
            return object : CubeMap() {
                override val id: Int = GL11.glGenTextures()

                init {
                    GL11.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, id)
                    faces.forEachIndexed { index, face ->
                        GL11.glTexImage2D(
                            GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + index,
                            0, GL11.GL_RGBA, width, height, 0,
                            GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, face
                        )
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
                    GL11.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP,0)
                }

            }
        }

        fun fromResource(faces: Array<String>): CubeMap {
            val facesBuffer = mutableListOf<IntBuffer>()
            var width = 0
            var height = 0

            Model::class.java.classLoader.also { loader ->
                faces.forEach { face ->
                    loader.getResourceAsStream(face)?.also { src ->
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

                            facesBuffer.add(
                                ByteBuffer.allocateDirect(raw.size shl 2)
                                    .order(ByteOrder.nativeOrder())
                                    .asIntBuffer()
                                    .put(raw)
                                    .flip()
                            )
                        }
                    }
                }
            }
            return fromRaw(facesBuffer.toTypedArray(), width, height)
        }
    }
}