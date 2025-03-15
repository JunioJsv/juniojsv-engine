package juniojsv.engine.features.utils.factories

import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.mesh.ObjMeshLoader

enum class ObjMeshes(val path: String)

object ObjMeshFactory {
    private val loader = ObjMeshLoader()
    fun create(mesh: ObjMeshes): Mesh {
        return loader.get(mesh.path)
    }
}