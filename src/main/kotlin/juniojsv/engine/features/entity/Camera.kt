package juniojsv.engine.features.entity

import juniojsv.engine.features.window.Window
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class Camera(
    @Suppress("UNUSED_PARAMETER")
    val position: Vector3f,
    private val window: Window
) {
    var fov = 90f
    var near = .1f
    var far = 10000000f

    @Suppress("UNUSED_PARAMETER")
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

    fun projection(): Matrix4f = Matrix4f().apply {
        window.getResolution().also { resolution ->
            val ratio = resolution.width.toFloat() / resolution.height.toFloat()
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
    }

    fun view(): Matrix4f {
        val nPosition = Vector3f(-position.x, -position.y, -position.z)
        return Matrix4f().apply {
            rotate(rotation.y, 1f, 0f, 0f)
            rotate(rotation.x, 0f, 1f, 0f)
            translate(nPosition)
        }
    }
}