package juniojsv.engine

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

interface Model {
    val instructionsCount: Int
    val identifier: Int

    companion object {
        fun fromRaw(vertices: FloatArray, instructions: IntArray): Model {
            return object : Model {
                override val instructionsCount: Int
                    get() = instructions.size
                override val identifier: Int
                    get() {
                        val vBuffer = BufferUtils.createFloatBuffer(vertices.size).apply {
                            put(vertices)
                            flip()
                        }
                        val iBuffer = BufferUtils.createIntBuffer(instructions.size).apply {
                            put(instructions)
                            flip()
                        }

                        return GL30.glGenVertexArrays().let { glVao ->
                            GL30.glBindVertexArray(glVao)
                            // Bind instructions
                            GL15.glGenBuffers().also { glBuffer ->
                                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, glBuffer)
                                GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, iBuffer, GL15.GL_STATIC_DRAW)
                            }
                            // Bind vertices
                            GL15.glGenBuffers().also { glBuffer ->
                                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, glBuffer)
                                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vBuffer, GL15.GL_STATIC_DRAW)
                                GL20.glVertexAttribPointer(
                                    0, 3, GL11.GL_FLOAT,
                                    false, 0, 0
                                )
                            }
                            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
                            GL30.glBindVertexArray(0)
                            glVao
                        }
                    }
            }
        }
    }
}