package juniojsv.engine.features.utils

import juniojsv.engine.features.entity.debugger.DebugSphere
import juniojsv.engine.features.entity.debugger.IDebugBeing
import org.joml.Vector3f

data class BoundarySphere(val radius: Float) : IBoundaryShape {
    override fun isInsideFrustum(frustum: Frustum, position: Vector3f, scale: Float): Boolean {
        return frustum.isSphereInside(position, radius * scale)
    }

    override fun getDebugBeing(position: Vector3f, scale: Float): IDebugBeing {
        return DebugSphere(position, radius * scale)
    }
}
