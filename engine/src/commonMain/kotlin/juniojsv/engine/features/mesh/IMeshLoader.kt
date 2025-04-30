package juniojsv.engine.features.mesh

import juniojsv.engine.features.utils.IBoundaryShape

interface IMeshLoader {
    fun get(file: String, boundary: IBoundaryShape? = null): Mesh
}