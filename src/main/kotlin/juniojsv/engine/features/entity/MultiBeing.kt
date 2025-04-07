package juniojsv.engine.features.entity

import juniojsv.engine.extensions.toBuffer
import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.texture.Texture
import juniojsv.engine.features.texture.Texture.Companion.bind
import org.joml.Vector3f
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL33
import org.lwjgl.system.MemoryUtil
import kotlin.properties.Delegates

class MultiBeing(
    private val mesh: Mesh,
    private val shader: ShadersProgram,
    isDebuggable: Boolean = true,
    private val isFrustumCullingEnabled: Boolean = true,
    private val isPhysicsEnabled: Boolean = true,
    private val isShaderOverridable: Boolean = true
) : BeingRender(isDebuggable, isFrustumCullingEnabled, isPhysicsEnabled, isShaderOverridable) {
    private val boundary = mesh.boundary
    private lateinit var beings: List<BaseBeing>
    private lateinit var textures: Set<Texture>
    private var transformationsVbo by Delegates.notNull<Int>()
    private var texturesIndexesVbo by Delegates.notNull<Int>()
    private var texturesScaleVbo by Delegates.notNull<Int>()
    private lateinit var context: IWindowContext

    private val disposeCallbacks = mutableListOf<() -> Unit>()

    constructor(
        mesh: Mesh,
        shader: ShadersProgram,
        beings: List<BaseBeing>,
        isDebuggable: Boolean = true,
        isFrustumCullingEnabled: Boolean = true,
        isPhysicsEnabled: Boolean = true,
        isShaderOverridable: Boolean = true
    ) : this(mesh, shader, isDebuggable, isFrustumCullingEnabled, isPhysicsEnabled, isShaderOverridable) {
        update(beings)
    }

    fun update(beings: List<BaseBeing>) {
        this.beings = beings
        textures = this.beings.mapNotNull { it.texture }.toSet()
        didSetup = false
    }

    override fun setup(context: IWindowContext) {
        super.setup(context)
        this.context = context
        disposeCallbacks.forEach { it.invoke() }
        disposeCallbacks.clear()
        if (isPhysicsEnabled && boundary != null)
            for (being in beings) {
                being.createRigidBody(context, boundary)
                disposeCallbacks.add { being.disposeRigidBody(context) }
            }
    }

    init {
        GL30.glBindVertexArray(mesh.vao)

        /// Transformation vbo
        GL30.glGenBuffers().also { vbo ->
            val attrIndex = 3
            transformationsVbo = vbo
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo)

            /// Attr index 3, 4, 5 will be used to store the transformation matrix
            for (i in 0..3) {
                GL30.glVertexAttribPointer(attrIndex + i, 4, GL30.GL_FLOAT, false, 16 * 4, (i * 4 * 4).toLong())
                GL30.glEnableVertexAttribArray(attrIndex + i)
                GL33.glVertexAttribDivisor(attrIndex + i, 1)
            }
        }

        /// Texture index vbo
        GL30.glGenBuffers().also { vbo ->
            val attrIndex = 7
            texturesIndexesVbo = vbo
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo)

            GL30.glVertexAttribIPointer(attrIndex, 1, GL30.GL_INT, 0, 0)
            GL30.glEnableVertexAttribArray(attrIndex)
            GL33.glVertexAttribDivisor(attrIndex, 1)
        }

        /// Texture scale vbo
        GL30.glGenBuffers().also { vbo ->
            val attrIndex = 8
            texturesScaleVbo = vbo
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo)

            GL30.glVertexAttribPointer(attrIndex, 1, GL30.GL_FLOAT, false, 0, 0)
            GL30.glEnableVertexAttribArray(attrIndex)
            GL33.glVertexAttribDivisor(attrIndex, 1)

        }

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0)
        GL30.glBindVertexArray(0)
    }

    private fun updateVbos(beings: List<BaseBeing>) {
        updateTransformationsVbo(beings)
        updateTexturesIndexesVbo(beings)
        updateTexturesScaleVbo(beings)
        GL30.glBindVertexArray(0)
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0)
    }

    private fun updateTexturesIndexesVbo(beings: List<BaseBeing>) {
        GL30.glBindVertexArray(mesh.vao)
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, texturesIndexesVbo)
        val textureIndexes = IntArray(beings.size)
        for ((index, being) in beings.withIndex()) {
            val textureIndex = textures.indexOfFirst { being.texture?.id == it.id }
            textureIndexes[index] = textureIndex
        }

        val textureIndexesBuffer = textureIndexes.toBuffer()
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, textureIndexesBuffer, GL30.GL_STREAM_DRAW)
        MemoryUtil.memFree(textureIndexesBuffer)
    }

    private fun updateTexturesScaleVbo(beings: List<BaseBeing>) {
        GL30.glBindVertexArray(mesh.vao)
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, texturesScaleVbo)
        val textureScales = FloatArray(beings.size)
        for ((index, being) in beings.withIndex()) {
            textureScales[index] = being.textureScale
        }

        val textureScalesBuffer = textureScales.toBuffer()
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, textureScalesBuffer, GL30.GL_STREAM_DRAW)
        MemoryUtil.memFree(textureScalesBuffer)
    }


    private fun updateTransformationsVbo(beings: List<BaseBeing>) {
        GL30.glBindVertexArray(mesh.vao)
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, transformationsVbo)
        val transformationsBuffer = MemoryUtil.memAllocFloat(beings.size * 16)
        for (being in beings) {
            val transformationMatrix = being.transform.transformation()
            val transformationArray = FloatArray(16)
            transformationMatrix.get(transformationArray)
            transformationsBuffer.put(transformationArray)
        }
        transformationsBuffer.flip()
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, transformationsBuffer, GL30.GL_STREAM_DRAW)
        MemoryUtil.memFree(transformationsBuffer)
    }

    override fun render(context: IWindowContext) {
        super.render(context)

        val light = context.render.ambientLight
        val frustum = context.camera.frustum
        val camera = context.camera.instance
        val shader = if (isShaderOverridable) Companion.shader ?: shader else shader

        val beings = if (!isFrustumCullingEnabled || boundary == null) beings else beings.filter {
            val isInsideFrustum = boundary.isInsideFrustum(frustum, it.transform)
            if (canDebug && isInsideFrustum)
                context.render.debugBeings.add(boundary.getDebugBeing(it.transform))
            isInsideFrustum
        }

        if (beings.isEmpty()) return

        updateVbos(beings)

        shader.apply {
            bind()
            putUniform("camera_projection", context.camera.projection)
            putUniform("camera_view", context.camera.view)
            putUniform("previous_camera_projection", context.camera.previousProjection)
            putUniform("previous_camera_view", context.camera.previousView)
            putUniform("camera_position", camera.position)
            putUniform("light_position", light?.position ?: Vector3f(0f))
            putUniform("light_color", light?.color ?: Vector3f(0f))
            putUniform("time", context.time.elapsedInSeconds.toFloat())

            textures.bind()
            putUniforms(this)
        }
        mesh.bind()
        GL33.glDrawElementsInstanced(GL33.GL_TRIANGLES, mesh.getIndicesCount(), GL33.GL_UNSIGNED_INT, 0, beings.size)
    }

    override fun dispose() {
        super.dispose()
        GL30.glDeleteBuffers(transformationsVbo)
        GL30.glDeleteBuffers(texturesIndexesVbo)
        GL30.glDeleteBuffers(texturesScaleVbo)
        disposeCallbacks.forEach { it.invoke() }
    }

    companion object {
        var shader: ShadersProgram? = null
    }
}