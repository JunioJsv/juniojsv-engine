package juniojsv.engine.features.entity

import juniojsv.engine.Config
import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.shader.ShadersProgram

abstract class BeingRender(
    private val isDebuggable: Boolean,
    private val isFrustumCullingEnabled: Boolean,
    private val isPhysicsEnabled: Boolean,
    private val isShaderOverridable: Boolean
) : IRender {
    val uniforms = mutableMapOf<String, Any>()

    protected var didSetup = false
    val canDebug: Boolean
        get() = isDebuggable && Config.isDebug

    protected open fun setup(context: IWindowContext) {
        didSetup = true
    }

    protected fun putUniforms(shader: ShadersProgram) {
        for ((name, value) in uniforms) {
            shader.putUniform(name, value)
        }
    }

    override fun render(context: IWindowContext) {
        if (!didSetup) setup(context)
    }
}