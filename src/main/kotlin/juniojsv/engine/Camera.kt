package juniojsv.engine

import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

enum class CameraMovement {
    FORWARD,
    BACKWARD,
    RIGHT,
    LEFT
}

class Camera(private val view: View, val position: Vector3f = Vector3f(0f)) {
    private var pitch: Float = 0f
    private var yaw: Float = 0f

    val fov = 90f
    val near = .1f
    val far = 1000f

    fun move(direction: CameraMovement, speed: Float) {
        val sinYaw = sin(yaw) * speed
        val cosYaw = cos(yaw) * speed
        val sinPitch = sin(pitch) * speed

        position.apply {
            when (direction) {
                CameraMovement.FORWARD -> {
                    x += sinYaw
                    z -= cosYaw
                    y -= sinPitch
                }
                CameraMovement.BACKWARD -> {
                    x -= sinYaw
                    z += cosYaw
                    y += sinPitch
                }
                CameraMovement.RIGHT -> {
                    x += cosYaw
                    z += sinYaw
                }
                CameraMovement.LEFT -> {
                    x -= cosYaw
                    z -= sinYaw
                }
            }
        }
    }

    fun rotate(offsetX: Double, offsetY: Double) {
        with(view) {
            if (offsetX < width && offsetY < height) {
                this@Camera.yaw = Math.toRadians(offsetX - width / 2).toFloat()
                this@Camera.pitch = Math.toRadians(offsetY - height / 2).toFloat()
            }
        }
    }

    fun projection(): Matrix4f = Matrix4f().apply {
        val ratio = view.width.toFloat() / view.height.toFloat()
        val yScale =
            (1f / tan(Math.toRadians((fov / 2f).toDouble())) * ratio).toFloat()
        val xScale = yScale / ratio
        val frustum: Float = far - near

        m00(xScale)
        m11(yScale)
        m22(-((far + near) / frustum))
        m23(-1f)
        m32(-(2 * near * far / frustum))
        m33(0f)
    }

    fun view(): Matrix4f {
        val nPosition = Vector3f(-position.x, -position.y, -position.z)
        return Matrix4f().apply {
            rotate(pitch, 1f, 0f, 0f)
            rotate(yaw, 0f, 1f, 0f)
            translate(nPosition)
        }
    }
}