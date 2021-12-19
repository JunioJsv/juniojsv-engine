package juniojsv.engine.features.mesh

import de.javagl.obj.ObjData
import de.javagl.obj.ObjReader
import de.javagl.obj.ObjUtils
import juniojsv.engine.features.utils.Resource
import java.io.InputStream

class ObjMeshProvider(private val file: String) : IMeshProvider {
    override fun decode(onSuccess: (mesh: Mesh) -> Unit) {
        if (!file.endsWith(".obj")) throw Exception("This file not is a WaveFront.")
        Resource.getResource(file) { stream ->
            decode(stream) { vertices, uv, normals, indices ->

                onSuccess(Mesh(vertices, uv, normals, indices))
            }
        }
    }

    private fun decode(
        stream: InputStream,
        onSuccess: (vertices: FloatArray, uv: FloatArray?, normals: FloatArray?, indices: IntArray?) -> Unit
    ) {
        val raw = ObjUtils.convertToRenderable(ObjReader.read(stream))
        onSuccess(
            ObjData.getVerticesArray(raw),
            ObjData.getTexCoordsArray(raw, 2),
            ObjData.getNormalsArray(raw),
            ObjData.getFaceVertexIndicesArray(raw)
        )
    }
}