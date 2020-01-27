package juniojsv.engine

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import java.io.BufferedReader
import java.io.InputStreamReader

abstract class Model {
    abstract val instructionsCount: Int
    abstract var identifier: Int

    companion object {
        fun fromRaw(vertices: FloatArray, normals: FloatArray, instructions: IntArray): Model {
            return object : Model() {
                override val instructionsCount: Int = instructions.size
                override var identifier: Int = GL30.glGenVertexArrays()

                init {
                    val vBuffer = BufferUtils.createFloatBuffer(vertices.size).apply {
                        put(vertices)
                        flip()
                    }
                    val iBuffer = BufferUtils.createIntBuffer(instructions.size).apply {
                        put(instructions)
                        flip()
                    }
                    val nBuffer = BufferUtils.createFloatBuffer(normals.size).apply {
                        put(normals)
                        flip()
                    }

                    identifier.also { glVao ->
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
                        // Bind normals
                        GL15.glGenBuffers().also { glBuffer ->
                            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, glBuffer)
                            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, nBuffer, GL15.GL_STATIC_DRAW)
                            GL20.glVertexAttribPointer(
                                2, 3, GL11.GL_FLOAT,
                                false, 0, 0
                            )
                        }
                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
                        GL30.glBindVertexArray(0)
                    }
                }
            }
        }

        fun fromResources(modelName: String, fileFormat: String): Model {
            val vertices = mutableListOf<Float>()
            val normals = mutableListOf<Float>()
            val instructions = mutableListOf<Int>()
            var buffer: String?

            when (fileFormat) {
                "obj" ->
                    Model::class.java.classLoader.also { loader ->
                        loader.getResourceAsStream("$modelName.obj")?.also { model ->
                            BufferedReader(InputStreamReader(model)).also { data ->
                                while (true) {
                                    buffer = data.readLine()
                                    buffer?.also { line ->
                                        when {
                                            line.startsWith("v ") ->
                                                line.substring(2)
                                                .split(' ')
                                                .filter { it.isNotEmpty() }
                                                .map { str ->
                                                    str.toFloat()
                                                }.also { vertices.addAll(it) }

                                            line.startsWith("vn ") ->
                                                line.substring(2)
                                                    .split(' ')
                                                    .filter { it.isNotEmpty() }
                                                    .map { str ->
                                                        str.toFloat()
                                                    }.also { normals.addAll(it) }

                                            line.startsWith("f ") ->
                                                line.substring(2).split(' ')
                                                .filter { it.isNotEmpty() }
                                                .forEach { values ->
                                                    values.split("/")
                                                        .filter { it.isNotEmpty() }
                                                        .map { str -> str.toInt() }.also {
                                                            instructions.add(it[0] - 1)
                                                        }
                                                }
                                        }
                                    } ?: break
                                }
                                data.close()
                            }
                            model.close()
                        }
                    }
                else -> throw Exception("Model format don't supported")
            }

            return fromRaw(
                vertices.toFloatArray(),
                normals.toFloatArray(),
                instructions.toIntArray()
            )
        }
    }
}