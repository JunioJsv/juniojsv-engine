package juniojsv.engine.features.utils.factories

import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.utils.BoundaryBox
import juniojsv.engine.features.utils.BoundaryEllipsoid
import juniojsv.engine.features.utils.Scale
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

object SkyboxMesh {
    fun create() = Mesh(
        floatArrayOf(
            -1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,

            -1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,

            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,

            -1.0f, -1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,

            -1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,

            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f
        )
    )
}

object CubeMesh {
    fun create() = Mesh(
        floatArrayOf(
            -1f, 1f, 1f,
            -1f, -1f, 1f,
            1f, -1f, 1f,
            1f, 1f, 1f,

            1f, 1f, -1f,
            1f, -1f, -1f,
            -1f, -1f, -1f,
            -1f, 1f, -1f,

            -1f, 1f, -1f,
            -1f, -1f, -1f,
            -1f, -1f, 1f,
            -1f, 1f, 1f,

            1f, 1f, 1f,
            1f, -1f, 1f,
            1f, -1f, -1f,
            1f, 1f, -1f,

            -1f, 1f, -1f,
            -1f, 1f, 1f,
            1f, 1f, 1f,
            1f, 1f, -1f,

            -1f, -1f, 1f,
            -1f, -1f, -1f,
            1f, -1f, -1f,
            1f, -1f, 1f
        ),
        floatArrayOf(
            0f, 1f, 0f, 0f, 1f, 0f, 1f, 1f,
            0f, 1f, 0f, 0f, 1f, 0f, 1f, 1f,
            0f, 1f, 0f, 0f, 1f, 0f, 1f, 1f,
            0f, 1f, 0f, 0f, 1f, 0f, 1f, 1f,
            0f, 1f, 0f, 0f, 1f, 0f, 1f, 1f,
            0f, 1f, 0f, 0f, 1f, 0f, 1f, 1f
        ),
        floatArrayOf(
            0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f,
            0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f,
            -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f,
            1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f,
            0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f,
            0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f
        ),
        indices = intArrayOf(
            0, 1, 2, 0, 2, 3,
            4, 5, 6, 4, 6, 7,
            8, 9, 10, 8, 10, 11,
            12, 13, 14, 12, 14, 15,
            16, 17, 18, 16, 18, 19,
            20, 21, 22, 20, 22, 23
        ),
        boundary = BoundaryBox(Vector3f(2f))
    )
}

class SphereMesh(details: Float = 1.0f) {
    private val sector = (36 * details).roundToInt()
    private val stack = (18 * details).roundToInt()
    private val radius = Scale.METER.length(1f)

    private val vertices: FloatArray
    private val uv: FloatArray
    private val normals: FloatArray
    private val indices: IntArray

    init {
        val verticesList = mutableListOf<Float>()
        val uvList = mutableListOf<Float>()
        val normalsList = mutableListOf<Float>()
        val indicesList = mutableListOf<Int>()

        val sectorStep = 2 * Math.PI / sector
        val stackStep = Math.PI / stack
        var sectorAngle: Double
        var stackAngle: Double
        var xy: Float
        var z: Float
        var x: Float
        var y: Float
        var nx: Float
        var ny: Float
        var nz: Float
        var s: Float
        var t: Float
        var k1: Int
        var k2: Int

        for (i in 0..stack) {
            stackAngle = Math.PI / 2 - i * stackStep
            xy = (radius * cos(stackAngle)).toFloat()
            z = (radius * sin(stackAngle)).toFloat()

            for (j in 0..sector) {
                sectorAngle = j * sectorStep
                x = (xy * cos(sectorAngle)).toFloat()
                y = (xy * sin(sectorAngle)).toFloat()

                nx = x / radius
                ny = y / radius
                nz = z / radius

                s = j.toFloat() / sector
                t = i.toFloat() / stack

                verticesList.add(x)
                verticesList.add(y)
                verticesList.add(z)

                uvList.add(s)
                uvList.add(t)

                normalsList.add(nx)
                normalsList.add(ny)
                normalsList.add(nz)

            }
        }

        for (i in 0 until stack) {
            k1 = i * (sector + 1)
            k2 = k1 + sector + 1

            for (j in 0 until sector) {
                if (i != 0) {
                    indicesList.add(k1)
                    indicesList.add(k2)
                    indicesList.add(k1 + 1)
                }

                if (i != (stack - 1)) {
                    indicesList.add(k1 + 1)
                    indicesList.add(k2)
                    indicesList.add(k2 + 1)
                }
                k1++
                k2++
            }
        }
        vertices = verticesList.toFloatArray()
        uv = uvList.toFloatArray()
        normals = normalsList.toFloatArray()
        indices = indicesList.toIntArray()
    }

    fun create(): Mesh =
        Mesh(
            vertices = vertices,
            uv = uv,
            normals = normals,
            indices = indices,
            boundary = BoundaryEllipsoid(Vector3f(1f))
        )
}

object QuadMesh {
    fun create() = Mesh(
        floatArrayOf(
            -1f, 1f, 0f,
            -1f, -1f, 0f,
            1f, -1f, 0f,
            1f, 1f, 0f
        ),
        floatArrayOf(
            0f, 1f,
            0f, 0f,
            1f, 0f,
            1f, 1f
        ),
        floatArrayOf(
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f
        ),
        indices = intArrayOf(
            0, 1, 2,
            0, 2, 3
        ),
        boundary = BoundaryBox(Vector3f(2f, 0f, 2f))
    )
}
