package juniojsv.engine.features.mesh

import juniojsv.engine.extensions.toBuffer
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import kotlin.properties.Delegates

open class Mesh(
    private val vertices: FloatArray,
    private val uv: FloatArray? = null,
    private val normals: FloatArray? = null,
    private val indices: IntArray? = null
) {
    val id: Int = GL30.glGenVertexArrays()
    private var count by Delegates.notNull<Int>()

    init {
        count = indices?.count() ?: (vertices.count() / 3)
    }

    fun getIndicesCount(): Int = count

    init {
        GL30.glBindVertexArray(id)

        GL15.glGenBuffers().also { glBuffer ->
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, glBuffer)
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices.toBuffer(), GL15.GL_STATIC_DRAW)
            GL20.glVertexAttribPointer(
                0, 3, GL11.GL_FLOAT,
                false, 0, 0
            )
        }

        uv?.toBuffer()?.let {
            GL15.glGenBuffers().also { glBuffer ->
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, glBuffer)
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, it, GL15.GL_STATIC_DRAW)
                GL20.glVertexAttribPointer(
                    1, 2, GL11.GL_FLOAT,
                    false, 0, 0
                )
            }
        }

        normals?.toBuffer()?.let {
            GL15.glGenBuffers().also { glBuffer ->
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, glBuffer)
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, it, GL15.GL_STATIC_DRAW)
                GL20.glVertexAttribPointer(
                    2, 3, GL11.GL_FLOAT,
                    false, 0, 0
                )
            }
        }

        indices?.toBuffer()?.let {
            GL15.glGenBuffers().also { glBuffer ->
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, glBuffer)
                GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, it, GL15.GL_STATIC_DRAW)
            }
        }

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        GL30.glBindVertexArray(0)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Mesh) return false

        if (!vertices.contentEquals(other.vertices)) return false
        if (uv != null) {
            if (other.uv == null) return false
            if (!uv.contentEquals(other.uv)) return false
        } else if (other.uv != null) return false
        if (normals != null) {
            if (other.normals == null) return false
            if (!normals.contentEquals(other.normals)) return false
        } else if (other.normals != null) return false
        if (indices != null) {
            if (other.indices == null) return false
            if (!indices.contentEquals(other.indices)) return false
        } else if (other.indices != null) return false
        if (count != other.count) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vertices.contentHashCode()
        result = 31 * result + (uv?.contentHashCode() ?: 0)
        result = 31 * result + (normals?.contentHashCode() ?: 0)
        result = 31 * result + (indices?.contentHashCode() ?: 0)
        result = 31 * result + count
        return result
    }
}