package juniojsv.engine

import org.joml.Matrix4f
import org.joml.Vector3f

enum class CAMERA_MOVEMENT {
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT,
    DOWN,
    UP
}

class Camera(private val view: View) {
    private val position: Vector3f = Vector3f(0f)
    private var elevation: Double = 0.0
    private var azimuth: Double = 0.0

    var fov = 90f
    var near = .1f
    var far = 1000f

    fun move(cameraMovement: CAMERA_MOVEMENT) {
        position.apply {
            when (cameraMovement) {
                CAMERA_MOVEMENT.FORWARD ->
                    if (z > 0)
                        z -= 1f
                CAMERA_MOVEMENT.BACKWARD ->
                    z += 1f
                else -> { }
            }
        }
    }

    fun rotate(azimuth: Double, elevation: Double) {
        with(view) {
            if (azimuth < width && elevation < height) {
                this@Camera.azimuth = (azimuth - width / 2) / (width / 2)
                this@Camera.elevation = (elevation - height / 2) / (height / 2)
            }
        }
    }

    fun view(): Matrix4f {
        val nPosition = Vector3f(-position.x, -position.y, -position.z)
        return Matrix4f().apply {
            translate(nPosition)
            rotate(Math.toRadians(elevation * (view.height / 2)).toFloat(), 1f, 0f, 0f)
            rotate(Math.toRadians(azimuth * (view.width / 2)).toFloat(), 0f, 1f, 0f)
        }
    }
}