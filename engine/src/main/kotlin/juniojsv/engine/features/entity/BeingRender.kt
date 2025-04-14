package juniojsv.engine.features.entity

import juniojsv.engine.Config
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram

abstract class BeingRender(
    val mesh: Mesh,
    private val isDebuggable: Boolean
) : Render() {
    val uniforms = mutableMapOf<String, Any>()

    val isDebug: Boolean
        get() = isDebuggable && Config.isDebug

    protected fun applyUniforms(shader: ShadersProgram) {
        for ((name, value) in uniforms) {
            shader.putUniform(name, value)
        }
    }
}