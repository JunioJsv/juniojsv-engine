package juniojsv.engine.features.entities

import juniojsv.engine.features.utils.Scale
import org.joml.Matrix4f
import org.joml.Vector3f

class Light(
    position: Vector3f = Vector3f(0f),
    var color: Vector3f = Vector3f(1f)
) {
    /**
     * The size of the orthographic projection used for the light's shadow map.
     */
    private val projectionSize: Float = Scale.METER.length(200f)

    /**
     * The near-clipping plane distance for the light's projection.
     */
    private val near: Float = 0.1f

    /**
     * The far clipping plane distance for the light's projection.
     */
    private val far: Float = Scale.METER.length(800f)

    /**
     * Represents the position of the light in 3D space as a vector.
     * The position determines the location from which the light source emits.
     */
    val position = Vector3f(position)

    /**
     * Represents the position of the shadow caster in world space.
     * The shadow caster position is derived from the light's position and adjusts
     * dynamically based on the direction and target of the light.
     */
    val shadowCasterPosition = Vector3f(position)

    /**
     * A 3D vector representing the target point for the shadow caster.
     *
     * This vector determines where the light is directed when rendering shadows, typically positioned
     * at the center of the area where the shadow map should be focused. It is used in conjunction with
     * the shadowCasterPosition and shadowCasterDirection to compute the light's view matrix and the
     * placement of its shadow map.
     */
    val shadowCasterTarget = Vector3f(0f)

    /**
     * Represents the direction vector of a shadow caster, pointing from the light source's position
     * towards its target. This normalized vector is used in the computation of light's shadowing
     * and projection matrices.
     */
    val shadowCasterDirection = Vector3f(0f)

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


    /**
     * Sets the target position for the shadow caster and adjusts its position based on the specified distance.
     * This method ensures the light is correctly oriented towards the target for shadow projection.
     *
     * @param target The target position that the shadow caster will focus on, represented as a 3D vector.
     * @param distance The distance from the target to the shadow caster's position, which determines how far
     *                 the shadow caster will be placed from the target along its direction.
     */
    fun setShadowCasterTarget(target: Vector3f, distance: Float) {
        shadowCasterTarget.set(target)

        shadowCasterDirection
            .set(position)
            .sub(shadowCasterTarget)
            .normalize()

        shadowCasterPosition
            .set(shadowCasterTarget)
            .add(shadowCasterDirection.mul(distance))

        updateMatrices()
    }

    /**
     * Updates the matrices for the light's projection and view transformations.
     *
     * This method recalculates the view and projection matrices based on the current state
     * of the shadow caster position, target position, and the projection parameters. It is used
     * to transform the light's view and projection spaces and combines them into a single
     * transformation matrix stored in `space`.
     *
     * Steps performed:
     * 1. Constructs the view matrix using the `lookAt` function, which aligns the light's view
     *    direction from `shadowCasterPosition` towards `shadowCasterTarget`, with `up` as the upward vector.
     * 2. Constructs the orthogonal projection matrix using the configured `projectionSize`, `near`, and `far` parameters.
     * 3. Combines the projection and view matrices into the `space` matrix using matrix multiplication.
     */
    private fun updateMatrices() {
        view.identity().lookAt(shadowCasterPosition, shadowCasterTarget, up)

        val left = -projectionSize
        val right = projectionSize
        val bottom = -projectionSize
        val top = projectionSize
        projection.identity().ortho(left, right, bottom, top, near, far)

        projection.mul(view, space)
    }
}