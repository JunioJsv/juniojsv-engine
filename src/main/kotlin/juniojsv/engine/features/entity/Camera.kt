package juniojsv.engine.features.entity

import juniojsv.engine.features.utils.Scale
import juniojsv.engine.features.window.Window
import org.joml.Math.toRadians
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.sin

class Camera(
    val position: Vector3f,
    private val window: Window
) {
    var fov = 75f
    var near = Scale.CENTIMETER.length(1f)
    var far = Scale.KILOMETER.length(100f)

    val rotation: Vector3f = Vector3f(0f)

    enum class CameraMovement {
        FORWARD, BACKWARD, LEFT, RIGHT, UP, DOWN
    }

    fun yaw() = toRadians(rotation.x.toDouble()).toFloat()

    fun pitch() = toRadians(rotation.y.toDouble()).toFloat()

    fun forward(): Vector3f {
        val yaw = yaw()
        val pitch = pitch()

        return Vector3f(sin(yaw) * cos(pitch), -sin(pitch), -(cos(yaw) * cos(pitch)))
    }

    fun right(): Vector3f {
        val forward = forward()
        return Vector3f(-1f * forward.z, 0f, forward.x)
    }

    fun up() = Vector3f(0f, 1f, 0f)

    fun move(movements: Set<CameraMovement>) {
        if (movements.isEmpty()) return
        val delta = window.context.time.deltaInSeconds
        val speed = (Scale.METER.length(500f) * delta).toFloat()

        val forward = forward()
        val right = right()
        val up = up()

        var direction = Vector3f()

        for (movement in movements) {
            direction.add(
                when (movement) {
                    CameraMovement.FORWARD -> forward
                    CameraMovement.BACKWARD -> forward.negate()
                    CameraMovement.RIGHT -> right
                    CameraMovement.LEFT -> right.negate()
                    CameraMovement.UP -> up
                    CameraMovement.DOWN -> up.negate()
                }
            )
        }

        if (direction.length() > 0f) {
            direction = direction.normalize().mul(speed)
            position.add(direction)
        }
    }

    fun rotate(x: Float, y: Float) {
        // Limit Pitch
        rotation.y = (rotation.y + y).coerceIn(-89f, 89f)

        // Normalize Yaw
        rotation.x += x
        if (rotation.x > 360) rotation.x -= 360f
        if (rotation.x < -360) rotation.x += 360f
    }

    fun projection(): Matrix4f {
        val resolution = window.resolution
        return Matrix4f().setPerspective(
            toRadians(fov.toDouble()).toFloat(),
            resolution.getAspectRatio(),
            near,
            far
        )
    }

    fun view(): Matrix4f {
        val center = Vector3f(position).add(forward())

        return Matrix4f().setLookAt(position, center, up())
    }
}
