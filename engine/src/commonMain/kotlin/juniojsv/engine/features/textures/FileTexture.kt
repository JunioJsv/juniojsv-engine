package juniojsv.engine.features.textures

import juniojsv.engine.features.utils.Resources
import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.constants.*

class FileTexture(file: String) : Texture() {
    init {
        val type = getType()
        GL.glBindTexture(type, id)

        val texture = Resources.texture(file)

        GL.glTexImage2D(
            type,
            0,
            GL_RGBA,
            texture.width, texture.height, 0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            texture.pixels
        )

        texture.dispose()

        GL.glGenerateMipmap(type)
        GL.glBindTexture(type, 0)

        GL.glTexParameteri(
            type, GL_TEXTURE_WRAP_S, GL_REPEAT
        )
        GL.glTexParameteri(
            type, GL_TEXTURE_WRAP_T, GL_REPEAT
        )
        GL.glTexParameteri(
            type, GL_TEXTURE_MIN_FILTER, GL_NEAREST
        )
        GL.glTexParameteri(
            type, GL_TEXTURE_MAG_FILTER, GL_LINEAR
        )
        GL.glTexParameteri(
            type, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST
        )
    }

    override fun getBindType(): Int {
        return GL_TEXTURE_BINDING_2D
    }

    override fun getType(): Int {
        return GL_TEXTURE_2D
    }
}