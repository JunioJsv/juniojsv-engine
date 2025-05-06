package juniojsv.engine.features.vbo

import juniojsv.engine.features.utils.Constants
import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.constants.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class FloatVbo(override val vao: Int, override val location: Int, override val divisor: Int) : Vbo() {
    init {
        GL.glBindVertexArray(vao)
        GL.glBindBuffer(GL_ARRAY_BUFFER, id)

        GL.glVertexAttribPointer(location, Constants.FLOAT_SIZE, GL_FLOAT, false, 0, 0)
        GL.glEnableVertexAttribArray(location)
        GL.glVertexAttribDivisor(location, divisor)

        GL.glBindBuffer(GL_ARRAY_BUFFER, 0)
        GL.glBindVertexArray(0)
    }

    fun update(data: List<Float>) {
        val bytes = (data.size * Constants.FLOAT_BYTE_SIZE).toLong()
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

            for (float in data) {
                buffer.putFloat(float)
            }
        } catch (e: Exception) {
            logger.error("Error updating ${javaClass.simpleName}: ${e.message}")
        } finally {
            if (buffer != null) GL.glUnmapBuffer(GL_ARRAY_BUFFER)
            GL.glBindBuffer(GL_ARRAY_BUFFER, 0)
        }
    }
}