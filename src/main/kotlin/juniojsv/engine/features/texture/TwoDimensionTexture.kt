package juniojsv.engine.features.texture

import juniojsv.engine.features.utils.Resource
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30

open class TwoDimensionTexture(private val file: String) : Texture() {
    init {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id)

        val (pixels, width, height) = getRawTexture(Resource.get(file))

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TwoDimensionTexture

        return file == other.file
    }

    override fun hashCode(): Int {
        return file.hashCode()
    }
}