package juniojsv.engine.features.utils.factories

import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.utils.Scale
import kotlin.math.cos
import kotlin.math.sin

object CubeMesh {
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

object SphereMesh {
    private const val SECTOR_COUNT = 36
    private const val STACK_COUNT = 18
    private val RADIUS = Scale.METER.length(1f)

    private val vertices: FloatArray
    private val uv: FloatArray
    private val normals: FloatArray
    private val indices: IntArray

    init {
        val verticesList = mutableListOf<Float>()
        val uvList = mutableListOf<Float>()
        val normalsList = mutableListOf<Float>()
        val indicesList = mutableListOf<Int>()

        val sectorStep = 2 * Math.PI / SECTOR_COUNT
        val stackStep = Math.PI / STACK_COUNT
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

        for (i in 0..STACK_COUNT) {
            stackAngle = Math.PI / 2 - i * stackStep
            xy = (RADIUS * cos(stackAngle)).toFloat()
            z = (RADIUS * sin(stackAngle)).toFloat()

            for (j in 0..SECTOR_COUNT) {
                sectorAngle = j * sectorStep
                x = (xy * cos(sectorAngle)).toFloat()
                y = (xy * sin(sectorAngle)).toFloat()

                nx = x / RADIUS
                ny = y / RADIUS
                nz = z / RADIUS

                s = j.toFloat() / SECTOR_COUNT
                t = i.toFloat() / STACK_COUNT

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

        for (i in 0 until STACK_COUNT) {
            k1 = i * (SECTOR_COUNT + 1)
            k2 = k1 + SECTOR_COUNT + 1

            for (j in 0 until SECTOR_COUNT) {
                if (i != 0) {
                    indicesList.add(k1)
                    indicesList.add(k2)
                    indicesList.add(k1 + 1)
                }

                if (i != (STACK_COUNT - 1)) {
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

    fun create(): Mesh = Mesh(vertices = vertices, uv = uv, normals = normals, indices = indices)
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
        )
    )
}
