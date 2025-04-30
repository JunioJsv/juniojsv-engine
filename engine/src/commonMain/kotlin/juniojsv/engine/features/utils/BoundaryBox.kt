package juniojsv.engine.features.utils

import com.bulletphysics.collision.shapes.BoxShape
import com.bulletphysics.collision.shapes.CollisionShape
import juniojsv.engine.extensions.toVecmath
import org.joml.Vector3f

class BoundaryBox(val extents: Vector3f) : IBoundaryShape {

    override fun isInsideFrustum(frustum: Frustum, transform: Transform): Boolean {
        val scale = Vector3f(extents).mul(transform.scale)

        if (transform.isRotated()) return frustum.isBoxInside(
            transform.position, scale, transform.rotation
        )

        return frustum.isBoxInside(transform.position, scale)
    }

    override fun createCollisionShape(scale: Vector3f): CollisionShape {
        return BoxShape(Vector3f(extents).apply {
            mul(scale)
            div(2f)
        }.toVecmath())
    }

    override fun createShapeTransform(transform: Transform): Transform {
        val scale = Vector3f(extents).apply {
            mul(transform.scale)
            div(2f)
        }

        return Transform(transform.position, transform.rotation, scale)
    }
}
