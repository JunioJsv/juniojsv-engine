package juniojsv.engine.essentials

import juniojsv.engine.extensions.toBuffer
import juniojsv.engine.utils.Resource
import org.joml.SimplexNoise
import org.joml.Vector3i
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.FloatBuffer
import java.nio.IntBuffer

abstract class Shell : Resource() {
    val id: Int = GL30.glGenVertexArrays()
    var indicesCount: Int = 0
        protected set

    protected abstract fun decode(stream: InputStream, onSuccess: (vertices: FloatArray,
                                                                   uvCoordinates: FloatArray?,
                                                                   normals: FloatArray?,
                                                                   indices: IntArray?) -> Unit)

    protected open fun bindAll(vertices: FloatBuffer,
                               uvCoordinates: FloatBuffer?,
                               normals: FloatBuffer?,
                               indices: IntBuffer?) {

        GL30.glBindVertexArray(id)

        // Bind vertices
        GL15.glGenBuffers().also { glBuffer ->
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, glBuffer)
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW)
            GL20.glVertexAttribPointer(
                    0, 3, GL11.GL_FLOAT,
                    false, 0, 0
            )
        }

        // Bind uvCoordinates
        uvCoordinates?.let {
            GL15.glGenBuffers().also { glBuffer ->
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, glBuffer)
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, it, GL15.GL_STATIC_DRAW)
                GL20.glVertexAttribPointer(
                        1, 2, GL11.GL_FLOAT,
                        false, 0, 0
                )
            }
        }

        // Bind normals
        normals?.let {
            GL15.glGenBuffers().also { glBuffer ->
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, glBuffer)
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, it, GL15.GL_STATIC_DRAW)
                GL20.glVertexAttribPointer(
                        2, 3, GL11.GL_FLOAT,
                        false, 0, 0
                )
            }
        }

        // Bind indices
        indices?.let {
            GL15.glGenBuffers().also { glBuffer ->
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, glBuffer)
                GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, it, GL15.GL_STATIC_DRAW)
            }
        }

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        GL30.glBindVertexArray(0)
    }

    class WaveFront(file: String) : Shell() {
        init {
            if (!file.endsWith(".obj")) throw Exception("This file not is a WaveFront.")
            getResources(file) { stream ->
                decode(stream) { vertices, uvCoordinates, normals, indices ->
                    indicesCount = indices?.size ?: 0
                    bindAll(
                            vertices.toBuffer(),
                            uvCoordinates!!.toBuffer(),
                            normals!!.toBuffer(),
                            indices!!.toBuffer()
                    )
                }
            }
        }

        override fun decode(stream: InputStream, onSuccess: (vertices: FloatArray, uvCoordinates: FloatArray?, normals: FloatArray?, indices: IntArray?) -> Unit) {
            val vertices = mutableListOf<Float>()
            val uvCoordinates = mutableListOf<Float>()
            val normals = mutableListOf<Float>()
            val faces = arrayOf<MutableList<Int>>(
                    mutableListOf(),    // [0] vertex_indices
                    mutableListOf(),    // [1] uvCoordinates_indices
                    mutableListOf()     // [2] normals_indices
            )

            // [0] uvCoordinates [1] normal
            val buffer = mutableListOf<FloatArray>()

            BufferedReader(InputStreamReader(stream)).also { file ->
                while (true) {
                    file.readLine()?.also { line ->
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
                file.close()
            }
            stream.close()
            onSuccess(vertices.toFloatArray(), buffer[0], buffer[1], faces[0].toIntArray())
        }
    }

    class Terrain(size: Int, faceScale: Float, noiseScale: Float, textureScale: Float) : Shell() {

        init {
            val vertices = mutableListOf<Float>()
            val uvCoordinates = mutableListOf<Float>()
            val normals = mutableListOf<Float>()
            val indices = mutableListOf<Int>()

            // Generate faces
            repeat(size) { z ->
                repeat(size) { x ->
                    val index = vertices.size / 3
                    val xPoint = x * faceScale
                    val zPoint = -z * faceScale
                    val yPoint = SimplexNoise.noise(x / 24f, z / 24f) * noiseScale

                    // Vertices
                    vertices.addAll(
                            arrayOf(xPoint, yPoint, zPoint)
                    )

                    // Uv coordinates
                    uvCoordinates.addAll(
                            arrayOf(
                                    x / (size - 1f) * textureScale,
                                    z / (size - 1f) * textureScale
                            )
                    )

                    // Normals
                    normals.addAll(
                            arrayOf(0f, 1f, 0f)
                    )

                    // Box indices
                    if (x < size - 1 && z < size - 1) {
                        indices.addAll(
                                arrayOf(
                                        index, index + 1, index + size,
                                        index + size + 1, index + size, index + 1
                                )
                        )
                    }
                }
            }

            indicesCount = indices.size
            bindAll(
                    vertices.toFloatArray().toBuffer(),
                    uvCoordinates.toFloatArray().toBuffer(),
                    normals.toFloatArray().toBuffer(),
                    indices.toIntArray().toBuffer()
            )
        }

        override fun decode(stream: InputStream, onSuccess: (vertices: FloatArray, uvCoordinates: FloatArray?, normals: FloatArray?, indices: IntArray?) -> Unit) {
            TODO("Not yet implemented")
        }
    }
}