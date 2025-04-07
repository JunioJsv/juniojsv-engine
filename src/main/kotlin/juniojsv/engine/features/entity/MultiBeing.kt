package juniojsv.engine.features.entity

import juniojsv.engine.extensions.toBuffer
import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.context.IWindowContextListener
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
) : BeingRender(isDebuggable, isFrustumCullingEnabled, isPhysicsEnabled, isShaderOverridable), IWindowContextListener {
    private val boundary = mesh.boundary
    private lateinit var beings: List<BaseBeing>
    private lateinit var textures: Set<Texture>
    private var transformationsVbo by Delegates.notNull<Int>()
    private var previousTransformationsVbo by Delegates.notNull<Int>()
    private var texturesIndexesVbo by Delegates.notNull<Int>()
    private var texturesScaleVbo by Delegates.notNull<Int>()
    private lateinit var context: IWindowContext

    private val disposeCallbacks = mutableListOf<() -> Unit>()

    private var lastTransforms by Delegates.notNull<Array<Transform>>()

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
        lastTransforms = Array(beings.size) { Transform().apply { set(beings[it].transform) } }
        didSetup = false
    }

    override fun setup(context: IWindowContext) {
        super.setup(context)
        this.context = context
        context.addListener(this)
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

        var index = 3

        /// aModel (3, 4, 5, 6)
        GL30.glGenBuffers().also { vbo ->
            val size = 4
            transformationsVbo = vbo
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo)

            for (i in 0..3) {
                GL30.glVertexAttribPointer(
                    index + i,
                    size,
                    GL30.GL_FLOAT,
                    false,
                    16 * size,
                    (i * size * size).toLong()
                )
                GL30.glEnableVertexAttribArray(index + i)
                GL33.glVertexAttribDivisor(index + i, 1)
            }

            index += size
        }

        /// aPreviousModel (7, 8, 9, 10)
        GL30.glGenBuffers().also { vbo ->
            val size = 4
            previousTransformationsVbo = vbo
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo)

            for (i in 0..3) {
                GL30.glVertexAttribPointer(
                    index + i,
                    size,
                    GL30.GL_FLOAT,
                    false,
                    16 * size,
                    (i * size * size).toLong()
                )
                GL30.glEnableVertexAttribArray(index + i)
                GL33.glVertexAttribDivisor(index + i, 1)
            }

            index += size
        }

        /// aTextureIndex (11)
        GL30.glGenBuffers().also { vbo ->
            val size = 1
            texturesIndexesVbo = vbo
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo)

            GL30.glVertexAttribIPointer(index, size, GL30.GL_INT, 0, 0)
            GL30.glEnableVertexAttribArray(index)
            GL33.glVertexAttribDivisor(index, 1)

            index += size
        }

        /// aTextureScale (12)
        GL30.glGenBuffers().also { vbo ->
            val size = 1
            texturesScaleVbo = vbo
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo)

            GL30.glVertexAttribPointer(index, size, GL30.GL_FLOAT, false, 0, 0)
            GL30.glEnableVertexAttribArray(index)
            GL33.glVertexAttribDivisor(index, 1)

            index += size
        }

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0)
        GL30.glBindVertexArray(0)
    }

    private fun updateVbos(indexes: List<Int>, beings: List<BaseBeing>) {
        updateTransformationsVbo(beings)
        updatePreviousTransformationsVbo(indexes)
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

    private fun updatePreviousTransformationsVbo(indexes: List<Int>) {
        GL30.glBindVertexArray(mesh.vao)
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, previousTransformationsVbo)
        val transformationsBuffer = MemoryUtil.memAllocFloat(beings.size * 16)
        for (index in indexes) {
            val transform = lastTransforms[index]
            val transformationMatrix = transform.transformation()
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

        var visibleBeings = beings.withIndex()
        if (isFrustumCullingEnabled && boundary != null) {
            visibleBeings = visibleBeings.filter {
                val being = it.value
                boundary.isInsideFrustum(frustum, being.transform).also { isInsideFrustum ->
                    if (canDebug && isInsideFrustum)
                        context.render.debugBeings.add(boundary.getDebugBeing(being.transform))
                }
            }
        }

        val beings = mutableListOf<BaseBeing>()
        val indexes = mutableListOf<Int>()

        for (being in visibleBeings) {
            indexes.add(being.index)
            beings.add(being.value)
        }

        if (beings.isEmpty()) return

        updateVbos(indexes, beings)

        shader.apply {
            bind()
            putUniform("uProjection", context.camera.projection)
            putUniform("uView", context.camera.view)
            putUniform("uPreviousProjection", context.camera.previousProjection)
            putUniform("uPreviousView", context.camera.previousView)
            putUniform("uCameraPosition", camera.position)
            putUniform("uLightPosition", light?.position ?: Vector3f(0f))
            putUniform("uLightColor", light?.color ?: Vector3f(0f))
            putUniform("uTime", context.time.elapsedInSeconds.toFloat())

            textures.bind()
            putUniforms(this)
        }
        mesh.bind()
        GL33.glDrawElementsInstanced(GL33.GL_TRIANGLES, mesh.getIndicesCount(), GL33.GL_UNSIGNED_INT, 0, beings.size)
    }

    override fun onPostRender(context: IWindowContext) {
        for ((index, being) in beings.withIndex()) {
            lastTransforms[index].set(being.transform)
        }
    }

    override fun dispose() {
        super.dispose()
        GL30.glDeleteBuffers(transformationsVbo)
        GL30.glDeleteBuffers(texturesIndexesVbo)
        GL30.glDeleteBuffers(texturesScaleVbo)
        context.removeListener(this)
        disposeCallbacks.forEach { it.invoke() }
    }

    companion object {
        var shader: ShadersProgram? = null
    }
}