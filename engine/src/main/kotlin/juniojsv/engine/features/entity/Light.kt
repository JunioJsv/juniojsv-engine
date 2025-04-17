package juniojsv.engine.features.entity

import juniojsv.engine.features.utils.Scale
import org.joml.Matrix4f
import org.joml.Vector3f

class Light(
    position: Vector3f = Vector3f(0f),
    var color: Vector3f = Vector3f(1f),
    target: Vector3f = Vector3f(0f, 0f, 0f),
) {
    /**
     * The size of the orthographic projection used for the light's shadow map.
     */
    private val projectionSize: Float = Scale.METER.length(200f)

    /**
     * The near clipping plane distance for the light's projection.
     */
    private val near: Float = 0.1f

    /**
     * The far clipping plane distance for the light's projection.
     */
    private val far: Float = Scale.METER.length(800f)

    /**
     * The original position of the light.
     */
    val origin = Vector3f(position)

    /**
     * The current position of the light.
     */
    var position = position
        set(value) {
            field.set(value)
            updateMatrices()
        }

    var target = target
        set(value) {
            field.set(value)
            updateMatrices()
        }

    /**
     * The view matrix of the light.
     */
    val view: Matrix4f = Matrix4f()

    /**
     * The projection matrix of the light.
     */
    val projection: Matrix4f = Matrix4f()

    /**
     * The light space matrix, which is the product of the projection and view matrices.
     */
    val space: Matrix4f = Matrix4f()

    /**
     * The up direction for the light's view matrix.
     */
    private val up: Vector3f = Vector3f(0f, 1f, 0f)

    init {
        updateMatrices()
    }

    fun lerpPosition(other: Vector3f, t: Float): Vector3f {
        return origin.lerp(other, t, position)
    }

    /**
     * Recalculates the view, projection, and combined light space matrices. Call this method whenever the light's
     * position, target, or projection parameters change.
     */
    private fun updateMatrices() {
        view.identity().lookAt(position, target, up)

        val left = -projectionSize
        val right = projectionSize
        val bottom = -projectionSize
        val top = projectionSize
        projection.identity().ortho(left, right, bottom, top, near, far)

        projection.mul(view, space)
    }
}