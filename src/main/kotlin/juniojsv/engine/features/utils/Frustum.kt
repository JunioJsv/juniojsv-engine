package juniojsv.engine.features.utils

import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.math.sqrt

class Frustum {
    private val planes = Array(6) { Vector4f() }

    constructor()

    constructor(projectionView: Matrix4f) {
        update(projectionView)
    }


    fun update(projectionView: Matrix4f) {
        with(projectionView) {
            planes[0].set(m03() + m00(), m13() + m10(), m23() + m20(), m33() + m30()) // Left
            planes[1].set(m03() - m00(), m13() - m10(), m23() - m20(), m33() - m30()) // Right
            planes[2].set(m03() + m01(), m13() + m11(), m23() + m21(), m33() + m31()) // Bottom
            planes[3].set(m03() - m01(), m13() - m11(), m23() - m21(), m33() - m31()) // Top
            planes[4].set(m03() + m02(), m13() + m12(), m23() + m22(), m33() + m32()) // Near
            planes[5].set(m03() - m02(), m13() - m12(), m23() - m22(), m33() - m32()) // Far
        }

        for (plane in planes) {
            val length = sqrt(plane.x * plane.x + plane.y * plane.y + plane.z * plane.z)
            if (length > 1e-6f) {
                plane.div(length)
            } else {
                plane.set(0f, 0f, 0f, 0f)
            }
        }
    }

    fun isRectangleInside(position: Vector3f, width: Float, height: Float, depth: Float): Boolean {
        for (plane in planes) {
            val pX = if (plane.x > 0) width / 2f else -width / 2f
            val pY = if (plane.y > 0) height / 2f else -height / 2f
            val pZ = if (plane.z > 0) depth / 2f else -depth / 2f
            val distance = plane.x * (position.x + pX) +
                    plane.y * (position.y + pY) +
                    plane.z * (position.z + pZ) +
                    plane.w
            if (distance < 0) {
                return false
            } else {
                continue
            }
        }
        return true
    }

    fun isSphereInside(position: Vector3f, radius: Float): Boolean {
        for (plane in planes) {
            val distance = plane.x * position.x + plane.y * position.y + plane.z * position.z + plane.w
            if (distance < -radius) return false
        }
        return true
    }

    fun iseEllipsoidInside(position: Vector3f, radius: Vector3f): Boolean {
        for (plane in planes) {
            // Calculate the distance from the sphere's center to the plane
            val centerDistance = plane.x * position.x + plane.y * position.y + plane.z * position.z + plane.w

            // Find the radius component along the normal of the plane.
            // Calculate the distance to the farthest point on the ellipsoid along the plane's normal
            val maxDistanceAlongNormal = sqrt(
                (plane.x * radius.x).let { it * it } +
                        (plane.y * radius.y).let { it * it } +
                        (plane.z * radius.z).let { it * it }
            )

            //If the closest point is outside the frustum, the entire ellipsoid is outside.
            if (centerDistance + maxDistanceAlongNormal < 0) {
                return false
            }
        }
        return true
    }
}
