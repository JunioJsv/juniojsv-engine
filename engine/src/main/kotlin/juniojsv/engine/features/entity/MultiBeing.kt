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
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.properties.Delegates

class MultiBeing(
    mesh: Mesh,
    private val shader: ShadersProgram,
    private val isDebuggable: Boolean = true,
    private val isFrustumCullingEnabled: Boolean = true,
    private val isPhysicsEnabled: Boolean = true,
    private val isShaderOverridable: Boolean = true,
    private val glMode: Int = GL30.GL_TRIANGLES,
    var isEnabled: Boolean = true,
) : BeingRender(mesh, isDebuggable), IWindowContextListener {
    private val boundary = mesh.boundary
    private val beings = mutableListOf<BaseBeing>()
    private val textures: MutableSet<Texture> = mutableSetOf()
    private val commands = ConcurrentLinkedQueue<() -> Unit>()

    private var transformationsVbo by Delegates.notNull<Int>()
    private var previousTransformationsVbo by Delegates.notNull<Int>()
    private var texturesIndexesVbo by Delegates.notNull<Int>()
    private var texturesScaleVbo by Delegates.notNull<Int>()

    constructor(
        mesh: Mesh,
        shader: ShadersProgram,
        beings: List<BaseBeing>,
        isDebuggable: Boolean = true,
        isFrustumCullingEnabled: Boolean = true,
        isPhysicsEnabled: Boolean = true,
        isShaderOverridable: Boolean = true
    ) : this(mesh, shader, isDebuggable, isFrustumCullingEnabled, isPhysicsEnabled, isShaderOverridable) {
        commands.add { replace(beings) }
    }

    fun replace(beings: List<BaseBeing>) {
        if (beings.isNotEmpty()) disposeBeings()
        for (being in beings) add(being)
    }

    fun add(being: BaseBeing) {
        if (boundary != null) {
            if (isPhysicsEnabled) being.createRigidBody(this)
            if (isDebuggable) being.createDebugger(this)
        }
        beings.add(being)
        being.texture?.let { textures.add(it) }
    }

    fun remove(being: BaseBeing) {
        beings.remove(being)
        being.dispose(context)
    }

    override fun setup(context: IWindowContext) {
        super.setup(context)
        context.addListener(this)
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

    private fun updateVbos(beings: List<BaseBeing>) {
        updateTransformationsVbo(beings)
        updatePreviousTransformationsVbo(beings)
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

    private fun updatePreviousTransformationsVbo(beings: List<BaseBeing>) {
        GL30.glBindVertexArray(mesh.vao)
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, previousTransformationsVbo)
        val transformationsBuffer = MemoryUtil.memAllocFloat(beings.size * 16)
        for (being in beings) {
            val transformationMatrix = being.transform.previous.transformation()
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

        var command = commands.poll()
        while (command != null) {
            command()
            command = commands.poll()
        }

        if (!isEnabled) return

        val beings: List<BaseBeing> = this.beings.filter { being ->
            var isVisible = being.isEnabled

            if (isVisible && isFrustumCullingEnabled && boundary != null) {
                val isInsideFrustum = being.isInsideFrustum(this)
                being.debugger?.being?.isEnabled = isInsideFrustum
                isVisible = isInsideFrustum
            }

            isVisible
        }

        if (beings.isEmpty()) return

        updateVbos(beings)

        val light = context.render.ambientLight
        val camera = context.camera.instance
        val shader = if (isShaderOverridable) Companion.shader ?: shader else shader

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
            applyUniforms(this)
        }
        mesh.bind()
        GL33.glDrawElementsInstanced(glMode, mesh.getIndicesCount(), GL33.GL_UNSIGNED_INT, 0, beings.size)
    }

    override fun onPostRender(context: IWindowContext) {
        beings.forEach { it.transform.setAsPrevious() }
    }

    private fun disposeBeings() {
        beings.forEach { it.dispose(context) }
        beings.clear()
    }

    override fun dispose() {
        super.dispose()
        GL30.glDeleteBuffers(transformationsVbo)
        GL30.glDeleteBuffers(texturesIndexesVbo)
        GL30.glDeleteBuffers(texturesScaleVbo)
        context.removeListener(this)
        disposeBeings()
    }

    companion object {
        var shader: ShadersProgram? = null
    }
}