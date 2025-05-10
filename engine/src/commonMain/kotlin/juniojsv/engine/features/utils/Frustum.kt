package juniojsv.engine.features.utils

import org.joml.*
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

    fun isBoxInside(position: Vector3f, extents: Vector3f): Boolean {
        val halfExtents = Vector3f(extents).div(2f)
        for (plane in planes) {

            val support = Vector3f(
                if (plane.x > 0) halfExtents.x else -halfExtents.x,
                if (plane.y > 0) halfExtents.y else -halfExtents.y,
                if (plane.z > 0) halfExtents.z else -halfExtents.z
            ).add(position)

            val distance = Vector4f(plane).dot(Vector4f(support, 1f))

            if (distance < 0) return false
        }
        return true
    }

    fun isBoxInside(position: Vector3f, extents: Vector3f, rotation: Quaternionf): Boolean {
        val rotationMatrix = Matrix3f().rotation(rotation)

        return isBoxInside(position, extents, rotationMatrix)
    }

    fun isBoxInside(position: Vector3f, extents: Vector3f, rotation: Vector3f): Boolean {
        val rotationMatrix = Matrix3f()
            .rotationX(Math.toRadians(rotation.x.toDouble()).toFloat())
            .rotateY(Math.toRadians(rotation.y.toDouble()).toFloat())
            .rotateZ(Math.toRadians(rotation.z.toDouble()).toFloat())

        return isBoxInside(position, extents, rotationMatrix)
    }

    fun isBoxInside(position: Vector3f, extents: Vector3f, rotation: Matrix3f): Boolean {
        val halfExtents = Vector3f(extents).div(2f)

        val rotationMatrix = Matrix3f().set(rotation)

        val localVertices = arrayOf(
            Vector3f(-halfExtents.x, -halfExtents.y, -halfExtents.z),
            Vector3f(halfExtents.x, -halfExtents.y, -halfExtents.z),
            Vector3f(-halfExtents.x, halfExtents.y, -halfExtents.z),
            Vector3f(halfExtents.x, halfExtents.y, -halfExtents.z),
            Vector3f(-halfExtents.x, -halfExtents.y, halfExtents.z),
            Vector3f(halfExtents.x, -halfExtents.y, halfExtents.z),
            Vector3f(-halfExtents.x, halfExtents.y, halfExtents.z),
            Vector3f(halfExtents.x, halfExtents.y, halfExtents.z)
        )

        val worldVertices = localVertices.map { v ->
            rotationMatrix.transform(Vector3f(v)).add(position)
        }

        for (plane in planes) {
            var allOutside = true

            for (vertex in worldVertices) {
                val distance = Vector4f(plane).dot(Vector4f(vertex, 1f))
                if (distance >= 0) {
                    allOutside = false
                    break
                }
            }

            if (allOutside) return false
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

    fun isEllipsoidInside(position: Vector3f, radius: Vector3f): Boolean {
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

    fun isEllipsoidInside(position: Vector3f, radius: Vector3f, rotation: Vector3f): Boolean {
        val rotationMatrix = Matrix3f()
            .rotationX(Math.toRadians(rotation.x.toDouble()).toFloat())
            .rotateY(Math.toRadians(rotation.y.toDouble()).toFloat())
            .rotateZ(Math.toRadians(rotation.z.toDouble()).toFloat())

        return isEllipsoidInside(position, radius, rotationMatrix)
    }

    fun isEllipsoidInside(position: Vector3f, radius: Vector3f, rotation: Quaternionf): Boolean {
        val rotationMatrix = Matrix3f().rotation(rotation)
        return isEllipsoidInside(position, radius, rotationMatrix)
    }

    fun isEllipsoidInside(position: Vector3f, radius: Vector3f, rotation: Matrix3f): Boolean {
        val rotationMatrix = Matrix3f().set(rotation)

        for (plane in planes) {
            val planeNormal = Vector3f(plane.x, plane.y, plane.z)

            val localNormal = rotationMatrix.transpose().transform(Vector3f(planeNormal)).normalize()

            val scaledNormal = Vector3f(
                localNormal.x * radius.x,
                localNormal.y * radius.y,
                localNormal.z * radius.z
            )

            val maxDistanceAlongNormal = scaledNormal.length()

            val centerDistance = planeNormal.dot(position) + plane.w

            if (centerDistance + maxDistanceAlongNormal < 0) {
                return false
            }
        }
        return true
    }
}