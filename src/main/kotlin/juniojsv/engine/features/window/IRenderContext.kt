package juniojsv.engine.features.window

import juniojsv.engine.features.entity.Camera
import juniojsv.engine.features.entity.Light
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.texture.Texture
import juniojsv.engine.features.ui.IImGuiLayout

interface IRenderContext {
    fun setCurrentShaderProgram(shader: ShadersProgram?)
    fun setCurrentTexture(texture: Texture?)
    fun setCurrentMesh(mesh: Mesh?)
    fun setCurrentAmbientLight(light: Light)
    fun setCurrentUi(layout: IImGuiLayout)
    fun getAmbientLight(): Light?
    fun getFramesCount(): Int
    fun getDelta(): Double
    fun getCamera(): Camera
}