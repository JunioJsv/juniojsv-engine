package juniojsv.engine.features.render

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.context.IWindowContextListener
import juniojsv.engine.features.entities.Entity
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.textures.AtlasCellTexture
import juniojsv.engine.features.textures.TextureUnits
import juniojsv.engine.features.utils.RenderTarget
import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.constants.GL_TRIANGLES
import juniojsv.engine.platforms.constants.GL_UNSIGNED_INT
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f

class EntityRender(
    mesh: Mesh,
    shader: ShadersProgram,
    private val entity: Entity = Entity(),
    isDebuggable: Boolean = true,
    isFrustumCullingEnabled: Boolean = true,
    private val isPhysicsEnabled: Boolean = true,
    isShaderOverridable: Boolean = true,
    private val glMode: Int = GL_TRIANGLES,
    isEnabled: Boolean = true,
) : MeshRenderer(
    RenderTarget.SINGLE,
    mesh,
    shader,
    isDebuggable,
    isShaderOverridable,
    isFrustumCullingEnabled,
    isEnabled
), IWindowContextListener {
    private val boundary = mesh.boundary

    override fun setup(context: IWindowContext) {
        super.setup(context)
        context.addListener(this)
        if (boundary != null) {
            if (isPhysicsEnabled) entity.createRigidBody(this)
            if (isDebuggable) entity.createDebugger(this)
        }
    }

    override fun render(context: IWindowContext) {
        super.render(context)

        if (!isEnabled || !entity.isEnabled) return

        if (isFrustumCullingEnabled() && boundary != null) {
            val isInsideFrustum = entity.isInsideFrustum(this)
            entity.debugger?.entity?.isEnabled = isInsideFrustum
            if (!isInsideFrustum) return
        }

        val transformation: Matrix4f = entity.transform.transformation()
        val light = context.render.ambientLight
        val camera = context.camera.instance

        entity.material?.texture?.let {
            it.bind()
            if (it is AtlasCellTexture) {
                uniforms["uUVScale"] = it.getUVScale()
                uniforms["uUVOffset"] = it.getUVOffset()
            } else {
                uniforms["uUVScale"] = Vector2f(entity.material.scale)
                uniforms["uUVOffset"] = Vector2f(0f)
            }
        } ?: TextureUnits.unbindAll()

        getShader().also {
            it.bind()
            uniforms["uProjection"] = context.camera.projection
            uniforms["uView"] = context.camera.view
            uniforms["uPreviousProjection"] = context.camera.previousProjection
            uniforms["uPreviousView"] = context.camera.previousView
            uniforms["uCameraPosition"] = camera.position
            uniforms["uModel"] = transformation
            uniforms["uPreviousModel"] = entity.transform.previous.transformation()
            uniforms["uLightPosition"] = light?.position ?: Vector3f(0f)
            uniforms["uLightColor"] = light?.color ?: Vector3f(0f)
            uniforms["uTime"] = context.time.current.toFloat()
            it.applyUniforms()
        }
        mesh.bind()
        GL.glDrawElements(
            glMode, mesh.getIndicesCount(), GL_UNSIGNED_INT, 0
        )
    }

    override fun onPostRender(context: IWindowContext) {
        if (!isPhysicsEnabled) entity.transform.setAsPrevious()
    }

    override fun dispose() {
        super.dispose()
        entity.dispose(context)
        context.removeListener(this)
    }
}