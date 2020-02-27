package juniojsv.engine

import org.joml.Matrix4f
import org.joml.Vector3f
import java.lang.UnsupportedOperationException
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

abstract class Entity {
    open val position: Vector3f = Vector3f(1f)
    open val rotation: Vector3f = Vector3f(1f)
    open var scale: Float = 1f

    open fun move(offsetX: Float, offsetY: Float, offsetZ: Float, increment: Boolean = false) {
        with(position) {
            x = if (increment) x + offsetX else offsetX
            y = if (increment) y + offsetY else offsetY
            z = if (increment) z + offsetZ else offsetZ
        }
    }

    open fun rotate(offsetX: Float, offsetY: Float, offsetZ: Float, increment: Boolean = false) {
        with(rotation) {
            x += if (increment) x + offsetX else offsetX
            y += if (increment) y + offsetY else offsetY
            z += if (increment) z + offsetZ else offsetZ
        }
    }

    class Light(override val position: Vector3f, val color: Vector3f = Vector3f(1f)) : Entity() {
        override fun rotate(offsetX: Float, offsetY: Float, offsetZ: Float, increment: Boolean) {
            throw UnsupportedOperationException("Don't use this!")
        }
    }

    class SkyBox(val cubeMap: Texture.CubeMap, val shader: Shader, override var scale: Float = 500f) : Entity() {
        fun transformation(): Matrix4f = Matrix4f().scale(scale)
    }

    class Camera(override val position: Vector3f, private val view: View) : Entity() {
        var fov = 90f
        var near = .1f
        var far = 1000f

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

        override fun move(offsetX: Float, offsetY: Float, offsetZ: Float, increment: Boolean) {
            throw UnsupportedOperationException("Don't use this!")
        }

        fun rotate(offsetX: Double, offsetY: Double) {
            with(view) {
                if (offsetX < width && offsetY < height) {
                    rotation.x = Math.toRadians(offsetX - width / 2).toFloat()
                    rotation.y = Math.toRadians(offsetY - height / 2).toFloat()
                }
            }
        }

        override fun rotate(offsetX: Float, offsetY: Float, offsetZ: Float, increment: Boolean) {
            throw UnsupportedOperationException("Don't use this!")
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
                rotate(rotation.y, 1f, 0f, 0f)
                rotate(rotation.x, 0f, 1f, 0f)
                translate(nPosition)
            }
        }
    }

    class Being(
        val model: Model,
        val texture: Texture.TwoDimension?,
        val shader: Shader?,
        override val position: Vector3f = Vector3f(0f),
        override val rotation: Vector3f = Vector3f(0f),
        override var scale: Float = 1f
    ) : Entity() {
        fun transformation(): Matrix4f = Matrix4f()
            .apply {
                translate(position)
                rotate(Math.toRadians(rotation.x.toDouble()).toFloat(), 1f, 0f, 0f)
                rotate(Math.toRadians(rotation.y.toDouble()).toFloat(), 0f, 1f, 0f)
                rotate(Math.toRadians(rotation.z.toDouble()).toFloat(), 0f, 0f, 1f)
                scale(scale)
            }
    }
}