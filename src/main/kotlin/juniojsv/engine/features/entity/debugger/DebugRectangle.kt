package juniojsv.engine.features.entity.debugger

import org.joml.Vector3f

data class DebugRectangle(
    val position: Vector3f, val width: Float, val height: Float, val depth: Float
) : IDebugBeing