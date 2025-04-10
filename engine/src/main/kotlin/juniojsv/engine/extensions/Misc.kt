package juniojsv.engine.extensions

fun org.joml.Vector3f.toVecmath(): javax.vecmath.Vector3f =
    javax.vecmath.Vector3f(x, y, z)

fun javax.vecmath.Vector3f.toJoml(): org.joml.Vector3f =
    org.joml.Vector3f(x, y, z)

fun org.joml.Matrix4f.toVecmath(): javax.vecmath.Matrix4f {
    val vals = FloatArray(16)
    this.get(vals)
    return javax.vecmath.Matrix4f(
        vals[0], vals[4], vals[8], vals[12],
        vals[1], vals[5], vals[9], vals[13],
        vals[2], vals[6], vals[10], vals[14],
        vals[3], vals[7], vals[11], vals[15]
    )
}

fun javax.vecmath.Matrix4f.toJoml(): org.joml.Matrix4f {
    return org.joml.Matrix4f(
        m00, m10, m20, m30,
        m01, m11, m21, m31,
        m02, m12, m22, m32,
        m03, m13, m23, m33
    )
}

fun org.joml.Quaternionf.toVecmath(): javax.vecmath.Quat4f {
    return javax.vecmath.Quat4f(x, y, z, w)
}

fun javax.vecmath.Quat4f.toJoml(): org.joml.Quaternionf {
    return org.joml.Quaternionf(x, y, z, w)
}