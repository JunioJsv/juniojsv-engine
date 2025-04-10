package juniojsv.engine.features.mesh

interface IMeshLoader {
    fun get(file: String): Mesh
}