package juniojsv.engine.features.mesh

import juniojsv.engine.extensions.toBuffer
import juniojsv.engine.features.utils.IBoundaryShape
import juniojsv.engine.features.utils.IDisposable
import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.PlatformMemory
import juniojsv.engine.platforms.constants.*
import kotlin.properties.Delegates

class Mesh(
    private val vertices: FloatArray,
    private val uv: FloatArray? = null,
    private val normals: FloatArray? = null,
    private val indices: IntArray? = null,
    val boundary: IBoundaryShape? = null
) : IDisposable {
    val vao: Int = GL.glGenVertexArrays()
    private val vbos: MutableList<Int> = mutableListOf()
    private var count by Delegates.notNull<Int>()

    fun getIndicesCount(): Int = count

    private fun calculateUVs(): FloatArray {
        val uvs = FloatArray(vertices.size / 3 * 2)
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
        return FloatArray(vertices.size)
    }

    init {
        GL.glBindVertexArray(vao)

        vertices.toBuffer().let {
            GL.glGenBuffers().also { vbo ->
                vbos.add(vbo)
                GL.glBindBuffer(GL_ARRAY_BUFFER, vbo)
                GL.glBufferData(GL_ARRAY_BUFFER, it, GL_STATIC_DRAW)
                GL.glVertexAttribPointer(
                    0, 3, GL_FLOAT,
                    false, 0, 0
                )
            }
            PlatformMemory.free(it)
        }

        (if (uv?.isNotEmpty() == true) uv else calculateUVs()).toBuffer().let {
            GL.glGenBuffers().also { vbo ->
                vbos.add(vbo)
                GL.glBindBuffer(GL_ARRAY_BUFFER, vbo)
                GL.glBufferData(GL_ARRAY_BUFFER, it, GL_STATIC_DRAW)
                GL.glVertexAttribPointer(
                    1, 2, GL_FLOAT,
                    false, 0, 0
                )
            }
            PlatformMemory.free(it)
        }

        (if (normals?.isNotEmpty() == true) normals else calculateNormals()).toBuffer().let {
            GL.glGenBuffers().also { vbo ->
                vbos.add(vbo)
                GL.glBindBuffer(GL_ARRAY_BUFFER, vbo)
                GL.glBufferData(GL_ARRAY_BUFFER, it, GL_STATIC_DRAW)
                GL.glVertexAttribPointer(
                    2, 3, GL_FLOAT,
                    false, 0, 0
                )
            }
            PlatformMemory.free(it)
        }

        indices?.toBuffer()?.let {
            GL.glGenBuffers().also { vbo ->
                vbos.add(vbo)
                GL.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo)
                GL.glBufferData(GL_ELEMENT_ARRAY_BUFFER, it, GL_STATIC_DRAW)
            }
            count = indices.count()
            PlatformMemory.free(it)
        } ?: run {
            count = vertices.count() / 3
        }

        GL.glBindBuffer(GL_ARRAY_BUFFER, 0)
        GL.glBindVertexArray(0)
    }

    fun bind() = bind(vao)

    override fun dispose() {
        vbos.forEach { GL.glDeleteBuffers(it) }
        GL.glDeleteVertexArrays(vao)
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
            val currentVao = GL.glGetInteger(GL_VERTEX_ARRAY_BINDING)
            if (vao == currentVao) return
            GL.glBindVertexArray(vao)
            if (vao != 0) {
                GL.glEnableVertexAttribArray(0)
                GL.glEnableVertexAttribArray(1)
                GL.glEnableVertexAttribArray(2)
            }
        }
    }
}