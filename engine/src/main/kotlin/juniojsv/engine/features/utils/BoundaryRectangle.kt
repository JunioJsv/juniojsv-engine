package juniojsv.engine.features.utils

import com.bulletphysics.collision.shapes.BoxShape
import com.bulletphysics.collision.shapes.CollisionShape
import juniojsv.engine.extensions.toVecmath
import juniojsv.engine.features.entity.debugger.DebugRectangle
import juniojsv.engine.features.entity.debugger.IDebugBeing
import org.joml.Vector3f

class BoundaryRectangle(val extents: Vector3f) : IBoundaryShape {

    override fun isInsideFrustum(frustum: Frustum, transform: Transform): Boolean {
        val scale = Vector3f(extents).mul(transform.scale)

        if (transform.hasRotation()) return frustum.isRectangleInside(
            transform.position, scale, transform.rotation
        )

        return frustum.isRectangleInside(transform.position, scale)
    }

    override fun getDebugBeing(transform: Transform): IDebugBeing {
        return DebugRectangle(transform.copy(scale = Vector3f(extents).apply {
            mul(transform.scale)
            div(2f)
        }))
    }

    override fun getCollisionShape(scale: Vector3f): CollisionShape {
        return BoxShape(Vector3f(extents).apply {
            mul(scale)
            div(2f)
        }.toVecmath())
    }
}
