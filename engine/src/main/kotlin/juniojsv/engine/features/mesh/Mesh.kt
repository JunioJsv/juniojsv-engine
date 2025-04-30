package juniojsv.engine.features.mesh

import juniojsv.engine.extensions.toBuffer
import juniojsv.engine.features.utils.IBoundaryShape
import juniojsv.engine.features.utils.IDisposable
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MemoryUtil
import kotlin.properties.Delegates

class Mesh(
    private val vertices: FloatArray,
    private val uv: FloatArray? = null,
    private val normals: FloatArray? = null,
    private val indices: IntArray? = null,
    val boundary: IBoundaryShape? = null
) : IDisposable {
    val vao: Int = GL30.glGenVertexArrays()
    private val vbos: MutableList<Int> = mutableListOf()
    private var count by Delegates.notNull<Int>()

    fun getIndicesCount(): Int = count

    private fun calculateUVs(): FloatArray {
        val uvs = FloatArray(vertices.size / 3 * 2) { 0f }
        var uvIndex = 0
        for (i in vertices.indices step 3) {
            val x = vertices[i]
            val y = vertices[i + 1]
            uvs[uvIndex++] = (x + 1) / 2
            uvs[uvIndex++] = (y + 1) / 2

        }
        return uvs
    }

    private fun calculateNormals(): FloatArray {
        return FloatArray(vertices.size) { 0f }
    }

    init {
        GL30.glBindVertexArray(vao)

        vertices.toBuffer().let {
            GL15.glGenBuffers().also { vbo ->
                vbos.add(vbo)
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, it, GL15.GL_STATIC_DRAW)
                GL20.glVertexAttribPointer(
                    0, 3, GL11.GL_FLOAT,
                    false, 0, 0
                )
            }
            MemoryUtil.memFree(it)
        }

        (if (uv?.isNotEmpty() == true) uv else calculateUVs()).toBuffer().let {
            GL15.glGenBuffers().also { vbo ->
                vbos.add(vbo)
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, it, GL15.GL_STATIC_DRAW)
                GL20.glVertexAttribPointer(
                    1, 2, GL11.GL_FLOAT,
                    false, 0, 0
                )
            }
            MemoryUtil.memFree(it)
        }

        (if (normals?.isNotEmpty() == true) normals else calculateNormals()).toBuffer().let {
            GL15.glGenBuffers().also { vbo ->
                vbos.add(vbo)
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, it, GL15.GL_STATIC_DRAW)
                GL20.glVertexAttribPointer(
                    2, 3, GL11.GL_FLOAT,
                    false, 0, 0
                )
            }
            MemoryUtil.memFree(it)
        }

        indices?.toBuffer()?.let {
            GL15.glGenBuffers().also { vbo ->
                vbos.add(vbo)
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo)
                GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, it, GL15.GL_STATIC_DRAW)
            }
            count = indices.count()
            MemoryUtil.memFree(it)
        } ?: run {
            count = vertices.count() / 3
        }

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        GL30.glBindVertexArray(0)
    }

    fun bind() = Companion.bind(vao)

    override fun dispose() {
        vbos.forEach { GL15.glDeleteBuffers(it) }
        GL30.glDeleteVertexArrays(vao)
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

    companion object {
        fun bind(vao: Int) {
            val currentVao = GL30.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING)
            if (vao == currentVao) return
            GL30.glBindVertexArray(vao)
            if (vao != 0) {
                // Todo(verify this)
                GL20.glEnableVertexAttribArray(0)
                GL20.glEnableVertexAttribArray(1)
                GL20.glEnableVertexAttribArray(2)
            }
        }
    }
}