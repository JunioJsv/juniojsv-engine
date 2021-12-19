package juniojsv.engine.features.window

import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.texture.Texture

interface IRenderContext {
    fun setCurrentShaderProgram(shader: ShadersProgram?)
    fun setCurrentTexture(texture: Texture?)
    fun setCurrentMesh(mesh: Mesh?)
    fun getFramesCount(): Int
    fun getDelta(): Double
}