package juniojsv.engine.features.textures

import juniojsv.engine.features.utils.Resources
import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.constants.GL_LINEAR
import juniojsv.engine.platforms.constants.GL_LINEAR_MIPMAP_NEAREST
import juniojsv.engine.platforms.constants.GL_REPEAT
import juniojsv.engine.platforms.constants.GL_RGBA
import juniojsv.engine.platforms.constants.GL_TEXTURE_2D
import juniojsv.engine.platforms.constants.GL_TEXTURE_BINDING_2D
import juniojsv.engine.platforms.constants.GL_TEXTURE_MAG_FILTER
import juniojsv.engine.platforms.constants.GL_TEXTURE_MIN_FILTER
import juniojsv.engine.platforms.constants.GL_TEXTURE_WRAP_S
import juniojsv.engine.platforms.constants.GL_TEXTURE_WRAP_T
import juniojsv.engine.platforms.constants.GL_UNSIGNED_BYTE
import kotlin.properties.Delegates

class FileTexture(val file: String) : Texture() {
    override var width: Int by Delegates.notNull()
    override var height: Int by Delegates.notNull()

    init {
        val type = getType()
        GL.glBindTexture(type, id)

        val texture = Resources.texture(file)

        GL.glTexParameteri(type, GL_TEXTURE_WRAP_S, GL_REPEAT)
        GL.glTexParameteri(type, GL_TEXTURE_WRAP_T, GL_REPEAT)
        GL.glTexParameteri(type, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        GL.glTexParameteri(type, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST)

        GL.glTexImage2D(
            type,
            0,
            GL_RGBA,
            texture.width, texture.height, 0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            texture.pixels
        )

        width = texture.width
        height = texture.height
        texture.dispose()

        GL.glGenerateMipmap(type)
        GL.glBindTexture(type, 0)
    }

    override fun getBindType(): Int {
        return GL_TEXTURE_BINDING_2D
    }

    override fun getType(): Int {
        return GL_TEXTURE_2D
    }
}