package juniojsv.engine.features.textures

import juniojsv.engine.features.utils.Resources
import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.constants.*
import org.joml.Vector2f
import kotlin.properties.Delegates

class AtlasCellTexture(val parent: FileAtlasTexture, val index: Int) : Texture() {
    override var id: Int = parent.id

    override val width: Int = parent.cellWidth
    override val height: Int = parent.cellHeight

    val x: Int = (index % parent.cols) * width
    val y: Int = (index / parent.cols) * height

    private val uvOffset = Vector2f(x.toFloat() / parent.width, y.toFloat() / parent.height)

    fun getUVScale(): Vector2f {
        return parent.getUVScale()
    }

    fun getUVOffset(): Vector2f {
        return uvOffset
    }

    override fun getBindType(): Int {
        return parent.getBindType()
    }

    override fun getType(): Int {
        return parent.getType()
    }
}

class FileAtlasTexture(file: String, val cols: Int, val rows: Int) : Texture() {
    override var width: Int by Delegates.notNull()
    override var height: Int by Delegates.notNull()
    private val uvScale = Vector2f(1f / cols, 1f / rows)
    val cellWidth: Int by lazy { width / cols }
    val cellHeight: Int by lazy { height / rows }

    init {
        val type = getType()
        GL.glBindTexture(type, id)

        val texture = Resources.texture(file)

        GL.glTexParameteri(type, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        GL.glTexParameteri(type, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        GL.glTexParameteri(type, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        GL.glTexParameteri(type, GL_TEXTURE_MIN_FILTER, GL_LINEAR)

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

        GL.glBindTexture(type, 0)
    }


    val cells = cols * rows

    fun getUVScale(): Vector2f {
        return uvScale
    }

    private val cache = mutableMapOf<Int, AtlasCellTexture>()

    fun getCell(index: Int): AtlasCellTexture {
        if (index < 0 || index >= cells) {
            throw IllegalArgumentException("Index out of bounds: $index")
        }
        return cache.getOrPut(index) { AtlasCellTexture(this, index) }
    }

    override fun getBindType(): Int {
        return GL_TEXTURE_BINDING_2D
    }

    override fun getType(): Int {
        return GL_TEXTURE_2D
    }
}