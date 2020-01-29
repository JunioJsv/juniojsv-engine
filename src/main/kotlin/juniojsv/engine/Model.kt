package juniojsv.engine

import org.joml.Vector3i
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import java.io.BufferedReader
import java.io.InputStreamReader

abstract class Model {
    abstract val id: Int
    abstract val indicesCount: Int

    companion object {
        fun fromRaw(
            vertices: FloatArray,
            uvCoordinates: FloatArray,
            normals: FloatArray,
            indices: IntArray
        ): Model {
            return object : Model() {
                override val indicesCount: Int = indices.size
                override var id: Int = GL30.glGenVertexArrays()

                init {
                    val vBuffer = BufferUtils.createFloatBuffer(vertices.size).apply {
                        put(vertices)
                        flip()
                    }
                    val uBuffer = BufferUtils.createFloatBuffer(uvCoordinates.size).apply {
                        put(uvCoordinates)
                        flip()
                    }
                    val nBuffer = BufferUtils.createFloatBuffer(normals.size).apply {
                        put(normals)
                        flip()
                    }
                    val iBuffer = BufferUtils.createIntBuffer(indices.size).apply {
                        put(indices)
                        flip()
                    }

                    id.also { glVao ->
                        GL30.glBindVertexArray(glVao)
                        // Bind indices
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
                        // Bind uvCoordinates
                        GL15.glGenBuffers().also { glBuffer ->
                            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, glBuffer)
                            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, uBuffer, GL15.GL_STATIC_DRAW)
                            GL20.glVertexAttribPointer(
                                1, 2, GL11.GL_FLOAT,
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

        fun fromResources(fileName: String): Model {
            val vertices = mutableListOf<Float>()
            val uvCoordinates = mutableListOf<Float>()
            val normals = mutableListOf<Float>()
            val faces = arrayOf<MutableList<Int>>(
                mutableListOf(),    // [0] vertice_indices
                mutableListOf(),    // [1] uvCoordinates_indices
                mutableListOf()     // [2] normals_indices
            )

            // [0] uvCoordinates [1] normal
            val buffer = mutableListOf<FloatArray>()

            when {
                fileName.endsWith(".obj") ->
                    Model::class.java.classLoader.also { loader ->
                        loader.getResourceAsStream(fileName)?.also { src ->
                            BufferedReader(InputStreamReader(src)).also { obj ->
                                while (true) {
                                    obj.readLine()?.also { line ->
                                        when {
                                            line.startsWith("v ") ->
                                                line.substring(2)
                                                    .split(' ')
                                                    .filter { it.isNotEmpty() }
                                                    .map { str ->
                                                        str.toFloat()
                                                    }.also { vertices.addAll(it) }

                                            line.startsWith("vt ") ->
                                                line.substring(2)
                                                    .split(' ')
                                                    .filter { it.isNotEmpty() }
                                                    .map { str ->
                                                        str.toFloat()
                                                    }.also { uvCoordinates.addAll(it) }

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
                                                            .map { str -> str.toInt() }.also { face ->
                                                                repeat(3) { index ->
                                                                    faces[index]
                                                                        .add(face[index] - 1)
                                                                }
                                                            }
                                                    }
                                        }
                                    } ?: break
                                }

                                repeat(2) {
                                    buffer.add(FloatArray(vertices.size))
                                }

                                repeat(faces[0].size) { index ->
                                    val face = Vector3i(
                                        faces[0][index],
                                        faces[1][index],
                                        faces[2][index]
                                    )
                                    with(buffer[0]) {
                                        val col = face.x * 2

                                        this[col] = uvCoordinates[face.y * 2]
                                        this[col + 1] =
                                            1 - uvCoordinates[face.y * 2 + 1]
                                    }

                                    with(buffer[1]) {
                                        val col = face.x * 3

                                        this[col] = normals[face.z * 3]
                                        this[col + 1] = normals[face.z * 3 + 1]
                                        this[col + 2] = normals[face.z * 3 + 2]
                                    }
                                }

                                obj.close()
                            }
                            src.close()
                        }
                    }
                else -> throw Exception("Model format don't supported")
            }

            return fromRaw(
                vertices.toFloatArray(),
                buffer[0],
                buffer[1],
                faces[0].toIntArray()
            )
        }
    }
}