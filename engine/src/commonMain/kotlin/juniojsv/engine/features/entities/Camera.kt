package juniojsv.engine.features.entities

import juniojsv.engine.features.utils.IMovable
import juniojsv.engine.features.utils.MovementDirection
import juniojsv.engine.features.utils.Scale
import juniojsv.engine.features.window.PlatformWindow
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.sin

class Camera(
    val position: Vector3f,
    private val window: PlatformWindow,
    private val parent: Any? = null,
) : IMovable {
    var fov = 75f
    var near = Scale.Companion.CENTIMETER.length(1f)
    var far = Scale.Companion.KILOMETER.length(100f)

    val rotation: Vector3f = Vector3f(0f)

    fun yaw() = Math.toRadians(rotation.x.toDouble()).toFloat()

    fun pitch() = Math.toRadians(rotation.y.toDouble()).toFloat()

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

    override fun move(movements: Set<MovementDirection>) {
        val parent = this.parent
        if (parent is IMovable) {
            parent.move(movements)
            return
        }

        if (movements.isEmpty()) return
        val deltaTime = window.context.time.deltaTime
        val speed = (Scale.Companion.METER.length(100f) * deltaTime).toFloat()

        val forward = forward()
        val right = right()
        val up = up()

        val direction = Vector3f()

        for (movement in movements) {
            when (movement) {
                MovementDirection.FORWARD -> direction.add(forward)
                MovementDirection.BACKWARD -> direction.sub(forward)
                MovementDirection.RIGHT -> direction.add(right)
                MovementDirection.LEFT -> direction.sub(right)
                MovementDirection.UP -> direction.add(up)
                MovementDirection.DOWN -> direction.sub(up)
            }
        }

        if (direction.length() > 0f) {
            direction.normalize().mul(speed)
            position.add(direction)
        }
    }

    override fun move(x: Float, y: Float) {
        val parent = this.parent
        if (parent is IMovable) {
            parent.move(x, y)
            return
        }

        val deltaTime = window.context.time.deltaTime
        val speed = (Scale.Companion.METER.length(100f) * deltaTime).toFloat()

        val forward = forward()
        val right = right()
        val direction = Vector3f()

        direction.add(forward.mul(y))
        direction.add(right.mul(x))

        if (direction.length() > 0f) {
            direction.normalize().mul(speed)
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
            Math.toRadians(fov.toDouble()).toFloat(),
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