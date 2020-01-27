package juniojsv.engine

import org.joml.Matrix4f
import org.joml.Vector3f

class Camera(private val view: View) {
    private val position: Vector3f = Vector3f(0f)
    private var elevation: Double = 0.0
    private var azimuth: Double = 0.0

    fun move(dx: Float, dy: Float, dz: Float, increment: Boolean = false) {
        position.apply {
            x = if (increment) x + dx else dx
            y = if (increment) y + dy else dy
            z = if (increment) z + dz else dz
        }
    }

    fun rotate(azimuth: Double, elevation: Double) {
        with(view) {
            if (azimuth < width && elevation < height) {
                this@Camera.azimuth = (azimuth - width / 2) / (width / 2)
                this@Camera.elevation = (elevation - height / 2) / (height / 2)
                println("""
                    [ENGINE] Camera Debug
                        Azimuth: ${this@Camera.azimuth}
                        Elevation: ${this@Camera.elevation}
                    """.trimIndent())
            }
        }
    }

    fun view(): Matrix4f {
        val nPosition = Vector3f(-position.x, -position.y, -position.z)
        return Matrix4f().apply {
            rotate(Math.toRadians(elevation * (view.height / 2)).toFloat(), 1f, 0f, 0f)
            rotate(Math.toRadians(azimuth * (view.width / 2)).toFloat(), 0f, 1f, 0f)
            translate(nPosition)
        }
    }
}