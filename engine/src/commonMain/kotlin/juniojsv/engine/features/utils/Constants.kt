package juniojsv.engine.features.utils

object Constants {
    // Attribute Sizes (number of components)
    const val VEC4_SIZE = 4
    const val VEC3_SIZE = 3
    const val VEC2_SIZE = 2

    const val FLOAT_SIZE = 1
    const val INT_SIZE = 1

    // Data Sizes (in bytes)
    const val BYTES_PER_FLOAT = 4
    const val BYTES_PER_INT = 4 // Usually same as float on modern systems

    const val VEC4_BYTE_SIZE = VEC4_SIZE * BYTES_PER_FLOAT
    const val VEC3_BYTE_SIZE = VEC3_SIZE * BYTES_PER_FLOAT
    const val VEC2_BYTE_SIZE = VEC2_SIZE * BYTES_PER_FLOAT
    const val FLOAT_BYTE_SIZE = FLOAT_SIZE * BYTES_PER_FLOAT
    const val INT_BYTE_SIZE = INT_SIZE * BYTES_PER_INT
    const val MAT4_FLOAT_COUNT = 16 // 4x4 matrix
    const val MAT4_BYTE_SIZE = MAT4_FLOAT_COUNT * BYTES_PER_FLOAT
}