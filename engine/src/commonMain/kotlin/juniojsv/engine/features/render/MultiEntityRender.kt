package juniojsv.engine.features.render

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.context.IWindowContextListener
import juniojsv.engine.features.entities.Entity
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.textures.AtlasCellTexture
import juniojsv.engine.features.textures.Texture
import juniojsv.engine.features.textures.Texture.Companion.bind
import juniojsv.engine.features.utils.RenderTarget
import juniojsv.engine.features.utils.ShadersConfig
import juniojsv.engine.features.vbo.IntVbo
import juniojsv.engine.features.vbo.Matrix4fVbo
import juniojsv.engine.features.vbo.Vector2fVbo
import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.constants.GL_TRIANGLES
import juniojsv.engine.platforms.constants.GL_UNSIGNED_INT
import org.joml.Vector2f
import org.joml.Vector3f
import java.util.concurrent.ConcurrentLinkedQueue

class MultiEntityRender(
    mesh: Mesh,
    shader: ShadersProgram,
    isDebuggable: Boolean = true,
    isFrustumCullingEnabled: Boolean = true,
    private val isPhysicsEnabled: Boolean = true,
    isShaderOverridable: Boolean = true,
    private val glMode: Int = GL_TRIANGLES,
    isEnabled: Boolean = true,
) : MeshRenderer(
    RenderTarget.MULTI,
    mesh,
    shader,
    isDebuggable,
    isShaderOverridable,
    isFrustumCullingEnabled,
    isEnabled
),
    IWindowContextListener {
    private val boundary = mesh.boundary
    private val entities = mutableListOf<Entity>()
    private val textures: MutableSet<Texture> = mutableSetOf()
    private val commands = ConcurrentLinkedQueue<() -> Unit>()

    companion object {
        private const val VBO_COUNT = 4

        private const val MODEL_VBO_INDEX = 0
        private const val PREVIOUS_MODEL_VBO_INDEX = MODEL_VBO_INDEX + 1
        private const val TEXTURE_VBO_INDEX = PREVIOUS_MODEL_VBO_INDEX + 1
        private const val UV_SCALE_VBO_INDEX = TEXTURE_VBO_INDEX + 1
        private const val UV_OFFSET_VBO_INDEX = UV_SCALE_VBO_INDEX + 1

        private const val INSTANCE_DIVISOR = 1 // Update attribute per instance
    }

    private val vbos = listOf(
        Matrix4fVbo(mesh.vao, ShadersConfig.Attributes.MODEL.location(), INSTANCE_DIVISOR),
        Matrix4fVbo(mesh.vao, ShadersConfig.Attributes.PREVIOUS_MODEL.location(), INSTANCE_DIVISOR),
        IntVbo(mesh.vao, ShadersConfig.Attributes.TEXTURE_INDEX.location(), INSTANCE_DIVISOR),
        Vector2fVbo(mesh.vao, ShadersConfig.Attributes.UV_SCALE.location(), INSTANCE_DIVISOR),
        Vector2fVbo(mesh.vao, ShadersConfig.Attributes.UV_OFFSET.location(), INSTANCE_DIVISOR)
    )

    constructor(
        mesh: Mesh,
        shader: ShadersProgram,
        entities: List<Entity>,
        isDebuggable: Boolean = true,
        isFrustumCullingEnabled: Boolean = true,
        isPhysicsEnabled: Boolean = true,
        isShaderOverridable: Boolean = true
    ) : this(
        mesh,
        shader,
        isDebuggable,
        isFrustumCullingEnabled,
        isPhysicsEnabled,
        isShaderOverridable
    ) {
        commands.add { replace(entities) }
    }

    fun replace(entities: List<Entity>) {
        if (entities.isNotEmpty()) disposeEntities()
        for (entity in entities) add(entity)
    }

    fun add(entity: Entity) {
        if (boundary != null) {
            if (isPhysicsEnabled) entity.createRigidBody(this)
            if (isDebuggable) entity.createDebugger(this)
        }
        entities.add(entity)
        entity.material?.texture?.let { textures.add(it) }
    }

    fun remove(entity: Entity) {
        if (entities.remove(entity)) {
            entity.dispose(context)
        }
    }

    override fun setup(context: IWindowContext) {
        super.setup(context)
        context.addListener(this)
    }

    private fun updateVbos(entities: List<Entity>) {
        updateModelVbo(entities)
        updatePreviousModelVbo(entities)
        updateTextureVbos(entities)
    }


    private fun updateModelVbo(entities: List<Entity>) {
        val vbo = vbos[MODEL_VBO_INDEX] as Matrix4fVbo
        vbo.update(entities.map { it.transform.transformation() })
    }

    private fun updatePreviousModelVbo(entities: List<Entity>) {
        val vbo = vbos[PREVIOUS_MODEL_VBO_INDEX] as Matrix4fVbo
        vbo.update(entities.map { it.transform.previous.transformation() })
    }

    private fun updateTextureVbos(entities: List<Entity>) {
        val textureIndexVbo = vbos[TEXTURE_VBO_INDEX] as IntVbo
        val uvScaleVbo = vbos[UV_SCALE_VBO_INDEX] as Vector2fVbo
        val uvOffsetVbo = vbos[UV_OFFSET_VBO_INDEX] as Vector2fVbo

        val textureIndexes = mutableListOf<Int>()
        val uvScales = mutableListOf<Vector2f>()
        val uvOffsets = mutableListOf<Vector2f>()

        for (entity in entities) {
            val material = entity.material
            val texture = material?.texture
            if (texture == null) {
                textureIndexes.add(-1)
                uvScales.add(Vector2f(1f))
                uvOffsets.add(Vector2f(0f))
                continue
            }
            val textureIndex = textures.indexOfFirst { it.id == texture.id }
            textureIndexes.add(textureIndex)
            if (texture is AtlasCellTexture) {
                uvScales.add(texture.getUVScale())
                uvOffsets.add(texture.getUVOffset())
            } else {
                uvScales.add(Vector2f(material.scale))
                uvOffsets.add(Vector2f(0f))
            }
        }

        textureIndexVbo.update(textureIndexes)
        uvScaleVbo.update(uvScales)
        uvOffsetVbo.update(uvOffsets)
    }

    override fun render(context: IWindowContext) {
        super.render(context)

        var command = commands.poll()
        while (command != null) {
            command()
            command = commands.poll()
        }

        if (!isEnabled) return

        val entities: List<Entity> = this.entities.filter { entity ->
            var isVisible = entity.isEnabled

            if (isVisible && isFrustumCullingEnabled() && boundary != null) {
                val isInsideFrustum = entity.isInsideFrustum(this)
                entity.debugger?.entity?.isEnabled = isInsideFrustum
                isVisible = isInsideFrustum
            }

            isVisible
        }

        if (entities.isEmpty()) return

        updateVbos(entities)

        val light = context.render.ambientLight
        val camera = context.camera.instance

        textures.bind()

        getShader().also {
            it.bind()
            uniforms["uProjection"] = context.camera.projection
            uniforms["uView"] = context.camera.view
            uniforms["uPreviousProjection"] = context.camera.previousProjection
            uniforms["uPreviousView"] = context.camera.previousView
            uniforms["uCameraPosition"] = camera.position
            uniforms["uLightPosition"] = light?.position ?: Vector3f(0f)
            uniforms["uLightColor"] = light?.color ?: Vector3f(0f)
            uniforms["uTime"] = context.time.current.toFloat()
            it.applyUniforms()
        }

        mesh.bind()
        GL.glDrawElementsInstanced(
            glMode,
            mesh.getIndicesCount(),
            GL_UNSIGNED_INT,
            0,
            entities.size
        )
    }

    override fun onPostRender(context: IWindowContext) {
        if (!isPhysicsEnabled) entities.forEach { it.transform.setAsPrevious() }
    }

    private fun disposeEntities() {
        entities.forEach { it.dispose(context) }
        entities.clear()
    }

    override fun dispose() {
        super.dispose()
        vbos.forEach { it.dispose() }
        context.removeListener(this)
        disposeEntities()
    }
}