package juniojsv.engine.features.entity

import juniojsv.engine.features.window.Window
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.sin

class Camera(
    val position: Vector3f,
    private val window: Window
) {
    var fov = 75f
    var near = .5f
    var far = 10000000f

    val rotation: Vector3f = Vector3f(0f)

    enum class CameraMovement {
        FORWARD,
        BACKWARD,
        RIGHT,
        LEFT,
        UP,
        DOWN
    }

    fun move(direction: CameraMovement, speed: Float) {
        with(position) {
            val sinRotX = sin(rotation.x) * speed
            val cosRotX = cos(rotation.x) * speed
            val sinRotY = sin(rotation.y) * speed
            when (direction) {
                CameraMovement.FORWARD -> {
                    x += sinRotX
                    z -= cosRotX
                    y -= sinRotY
                }

                CameraMovement.BACKWARD -> {
                    x -= sinRotX
                    z += cosRotX
                    y += sinRotY
                }

                CameraMovement.RIGHT -> {
                    x += cosRotX
                    z += sinRotX
                }

                CameraMovement.LEFT -> {
                    x -= cosRotX
                    z -= sinRotX
                }

                CameraMovement.UP -> y += speed
                CameraMovement.DOWN -> y -= speed
            }
        }
    }

    fun projection(): Matrix4f {
        val resolution = window.getResolution()
        return Matrix4f().setPerspective(
            Math.toRadians(fov.toDouble()).toFloat(),
            resolution.getAspectRatio(),
            near,
            far
        )
    }

    fun view(): Matrix4f {
        return Matrix4f().apply {
            rotate(rotation.y, 1f, 0f, 0f)
            rotate(rotation.x, 0f, 1f, 0f)
            translate(Vector3f(position).negate())
        }
    }
}
