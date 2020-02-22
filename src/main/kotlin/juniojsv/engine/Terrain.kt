package juniojsv.engine

import org.joml.SimplexNoise.noise

abstract class Terrain {
    companion object {
        fun generate(size: Int, faceScale: Float, noiseScale: Float, textureScale: Float): Model {
            val vertices = mutableListOf<Float>()
            val uvCoordinates = mutableListOf<Float>()
            val normals = mutableListOf<Float>()
            val indices = mutableListOf<Int>()

            // Generate faces
            repeat(size) { z ->
                repeat(size) { x ->
                    val index = vertices.size / 3
                    val xVert = x * faceScale
                    val zVert = -z * faceScale
                    val yVert = noise(x / 24f, z / 24f) * noiseScale

                    // Vertices
                    vertices.addAll(
                        arrayOf(xVert, yVert, zVert)
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

            return Model.fromRaw(
                vertices.toFloatArray(),
                uvCoordinates.toFloatArray(),
                normals.toFloatArray(),
                indices.toIntArray()
            )
        }
    }
}