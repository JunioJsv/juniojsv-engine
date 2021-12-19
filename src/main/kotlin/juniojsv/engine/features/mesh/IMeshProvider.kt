package juniojsv.engine.features.mesh

interface IMeshProvider {
    fun decode(onSuccess: (mesh: Mesh) -> Unit)
}