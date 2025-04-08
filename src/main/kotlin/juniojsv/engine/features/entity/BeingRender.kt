package juniojsv.engine.features.entity

import juniojsv.engine.Config
import juniojsv.engine.features.shader.ShadersProgram

abstract class BeingRender(
    private val isDebuggable: Boolean
) : Render() {
    val uniforms = mutableMapOf<String, Any>()

    val canDebug: Boolean
        get() = isDebuggable && Config.isDebug

    protected fun putUniforms(shader: ShadersProgram) {
        for ((name, value) in uniforms) {
            shader.putUniform(name, value)
        }
    }
}