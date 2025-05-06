package juniojsv.engine.features.vbo

import juniojsv.engine.features.utils.Constants
import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.constants.*
import org.joml.Matrix4f
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Matrix4fVbo(override val vao: Int, override val location: Int, override val divisor: Int = 0) : Vbo() {
    init {
        GL.glBindVertexArray(vao)
        GL.glBindBuffer(GL_ARRAY_BUFFER, id)

        for (i in 0 until Constants.VEC4_SIZE) {
            val location = location + i
            GL.glVertexAttribPointer(
                location,
                Constants.VEC4_SIZE,
                GL_FLOAT,
                false,
                Constants.MAT4_BYTE_SIZE,
                i * Constants.VEC4_SIZE * Constants.BYTES_PER_FLOAT
            )
            GL.glEnableVertexAttribArray(location)
            GL.glVertexAttribDivisor(location, divisor)
        }

        GL.glBindBuffer(GL_ARRAY_BUFFER, 0)
        GL.glBindVertexArray(0)
    }

    fun update(data: List<Matrix4f>) {
        val bytes = (data.size * Constants.MAT4_BYTE_SIZE).toLong()
        if (bytes == 0L) return
        GL.glBindBuffer(GL_ARRAY_BUFFER, id)
        GL.glBufferData(GL_ARRAY_BUFFER, bytes, GL_STREAM_DRAW)

        var buffer: ByteBuffer? = null

        try {
            buffer = GL.glMapBufferRange(
                GL_ARRAY_BUFFER,
                0,
                bytes,
                GL_MAP_WRITE_BIT or GL_MAP_INVALIDATE_BUFFER_BIT
            )
            if (buffer == null) return
            buffer.order(ByteOrder.nativeOrder())

            for ((index, matrix) in data.withIndex()) {
                val position = index * Constants.MAT4_BYTE_SIZE
                matrix.get(position, buffer)
            }
        } catch (e: Exception) {
            logger.error("Error updating ${javaClass.simpleName}: ${e.message}")
        } finally {
            if (buffer != null) GL.glUnmapBuffer(GL_ARRAY_BUFFER)
            GL.glBindBuffer(GL_ARRAY_BUFFER, 0)
        }
    }
}