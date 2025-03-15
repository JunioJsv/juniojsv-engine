package juniojsv.engine.features.utils

import juniojsv.engine.features.entity.debugger.DebugEllipsoid
import juniojsv.engine.features.entity.debugger.IDebugBeing
import org.joml.Vector3f

data class BoundaryEllipsoid(val radius: Vector3f) : IBoundaryShape {
    override fun isInsideFrustum(frustum: Frustum, position: Vector3f, scale: Vector3f): Boolean {
        return frustum.iseEllipsoidInside(position, Vector3f(radius).mul(scale))
    }

    override fun getDebugBeing(position: Vector3f, scale: Vector3f): IDebugBeing {
        return DebugEllipsoid(position, Vector3f(radius).mul(scale))
    }
}
