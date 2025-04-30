package juniojsv.engine.features.texture

import juniojsv.engine.features.utils.Resources
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import java.nio.file.Path

class FileTexture(file: String) : Texture() {
    val fileName = Path.of(file).fileName.toString()

    init {
        val type = getType()
        GL11.glBindTexture(type, id)

        val texture = getRawTexture(Resources.get(file))

        GL11.glTexImage2D(
            type,
            0, GL11.GL_RGBA,
            texture.width, texture.height, 0,
            GL11.GL_RGBA,
            GL11.GL_UNSIGNED_BYTE,
            texture.pixels
        )

        texture.dispose()

        GL30.glGenerateMipmap(type)
        GL11.glBindTexture(type, 0)

        GL11.glTexParameteri(
            type, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT
        )
        GL11.glTexParameteri(
            type, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT
        )
        GL11.glTexParameteri(
            type, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST
        )
        GL11.glTexParameteri(
            type, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR
        )
        GL11.glTexParameteri(
            type, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST
        )
    }

    override fun getBindType(): Int {
        return GL30.GL_TEXTURE_BINDING_2D
    }

    override fun getType(): Int {
        return GL11.GL_TEXTURE_2D
    }
}