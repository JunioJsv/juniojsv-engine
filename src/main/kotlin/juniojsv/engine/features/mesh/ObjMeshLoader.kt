package juniojsv.engine.features.mesh

import de.javagl.obj.ObjData
import de.javagl.obj.ObjReader
import de.javagl.obj.ObjUtils
import juniojsv.engine.features.utils.Resource
import java.util.concurrent.Executors

private data class RawObjMesh(
    val vertices: FloatArray,
    val uv: FloatArray? = null,
    val normals: FloatArray? = null,
    val indices: IntArray? = null
)

class ObjMeshLoader : IMeshLoader {
    override fun get(file: String): Mesh {
        if (!file.endsWith(".obj")) throw Exception("This file not is a WaveFront.")
        val (vertices, uv, normals, indices) = Executors.newSingleThreadExecutor().submit<RawObjMesh> {
            val raw = ObjUtils.convertToRenderable(ObjReader.read(Resource.get(file)))

            RawObjMesh(
                ObjData.getVerticesArray(raw),
                ObjData.getTexCoordsArray(raw, 2),
                ObjData.getNormalsArray(raw),
                ObjData.getFaceVertexIndicesArray(raw)
            )
        }.get()

        return Mesh(vertices, uv, normals, indices)
    }
}