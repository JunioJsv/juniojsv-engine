package juniojsv.engine.features.texture

import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.utils.IDisposable
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL30
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
    val id: Int = GL11.glGenTextures()

    abstract fun getTextureBindType(): Int

    abstract fun getTextureType(): Int

    fun bind() = listOf(this).bind()

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
        private val units = listOf(
            GL13.GL_TEXTURE0, GL13.GL_TEXTURE1, GL13.GL_TEXTURE2, GL13.GL_TEXTURE3,
            GL13.GL_TEXTURE4, GL13.GL_TEXTURE5, GL13.GL_TEXTURE6, GL13.GL_TEXTURE7,
            GL13.GL_TEXTURE8, GL13.GL_TEXTURE9, GL13.GL_TEXTURE10, GL13.GL_TEXTURE11,
            GL13.GL_TEXTURE12, GL13.GL_TEXTURE13, GL13.GL_TEXTURE14, GL13.GL_TEXTURE15
        )

        // Texture unit to Texture id
        private val bindings = mutableMapOf<Int, Texture>()

        private fun clearBindings() {
            bindings.forEach { (unit, texture) ->
                GL30.glActiveTexture(unit)
                val currentTexture = GL30.glGetInteger(texture.getTextureBindType())
                val shouldUnbind = texture.id == currentTexture
                if (shouldUnbind) {
                    GL30.glBindTexture(texture.getTextureType(), 0)
                }
            }
            bindings.clear()
        }

        fun Array<Texture>.bind() = toList().bind()

        fun Collection<Texture>.bind(): Unit {
            val textures = this.toSet()
            if (textures.isEmpty()) clearBindings()
            assert(textures.size <= units.size)
            val amountTextureBindings = textures.withIndex().count { (index, texture) ->
                val unit = units[index]
                GL30.glActiveTexture(unit)
                val currentTexture = GL30.glGetInteger(texture.getTextureBindType())
                val shouldBind = texture.id != currentTexture

                if (shouldBind) {
                    GL30.glBindTexture(texture.getTextureType(), texture.id)
                    bindings[unit] = texture
                }

                shouldBind
            }

            ShadersProgram.getCurrentShaderProgramId().takeIf { it != 0 }?.let { id ->
                ShadersProgram.putUniform(
                    id,
                    "textures",
                    IntArray(amountTextureBindings) { it }
                )
            }

            GL30.glActiveTexture(units[0])
        }
    }
}