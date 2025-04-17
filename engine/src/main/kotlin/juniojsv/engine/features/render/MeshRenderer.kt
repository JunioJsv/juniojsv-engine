package juniojsv.engine.features.render

import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.utils.RenderTarget

abstract class MeshRenderer(
    private val target: RenderTarget,
    val mesh: Mesh,
    private val shader: ShadersProgram,
    val isDebuggable: Boolean,
    val isShaderOverridable: Boolean,
    private val isFrustumCullingEnabled: Boolean,
    var isEnabled: Boolean
) : Render() {
    val uniforms = mutableMapOf<String, Any>()

    private val overrides get() = context.render.overrides

    fun getShader(): ShadersProgram =
        if (isShaderOverridable) overrides.shaders[target] ?: shader else shader

    fun isFrustumCullingEnabled(): Boolean = isFrustumCullingEnabled && overrides.isFrustumCullingEnabled

    fun ShadersProgram.applyUniforms() {
        for ((name, value) in uniforms + overrides.uniforms) {
            putUniform(name, value)
        }
    }
}