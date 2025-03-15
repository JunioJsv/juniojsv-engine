package juniojsv.engine.features.entity

import juniojsv.engine.Flags
import juniojsv.engine.extensions.toBuffer
import juniojsv.engine.features.context.WindowContext
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.texture.Texture
import org.joml.Vector3f
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL33
import org.lwjgl.system.MemoryUtil
import kotlin.properties.Delegates

class MultiBeing(
    private val mesh: Mesh,
    private val shader: ShadersProgram?,
    private val isDebuggable: Boolean = true,
    private val isFrustumCullingEnabled: Boolean = true
) : IRender {

    private lateinit var beings: List<BaseBeing>
    private lateinit var textures: Set<Texture>
    private var transformationsVbo by Delegates.notNull<Int>()
    private var texturesIndexesVbo by Delegates.notNull<Int>()
    private var texturesScaleVbo by Delegates.notNull<Int>()

    private val canDebug: Boolean
        get() = isDebuggable && Flags.debug

    constructor(
        mesh: Mesh,
        shader: ShadersProgram?,
        beings: List<BaseBeing>,
        isDebuggable: Boolean = true
    ) : this(mesh, shader, isDebuggable) {
        update(beings)
    }

    fun update(beings: List<BaseBeing>) {
        this.beings = beings
        textures = this.beings.mapNotNull { it.texture }.toSet()
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
            val transformationMatrix = being.transformation()
            val transformationArray = FloatArray(16)
            transformationMatrix.get(transformationArray)
            transformationsBuffer.put(transformationArray)
        }
        transformationsBuffer.flip()
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, transformationsBuffer, GL30.GL_STREAM_DRAW)
        MemoryUtil.memFree(transformationsBuffer)
    }

    override fun render(context: WindowContext) {
        val light = context.render.ambientLight
        val frustum = context.camera.frustum
        val camera = context.camera.instance
        val boundary = mesh.boundary

        val beings = if (!isFrustumCullingEnabled || boundary == null) beings else beings.filter {
            val position = it.transformedPosition()
            val scale = it.scale
            val isInsideFrustum = boundary.isInsideFrustum(frustum, position, scale)
            if (canDebug && isInsideFrustum)
                context.render.debugBeings.add(boundary.getDebugBeing(position, scale))
            isInsideFrustum
        }

        if (beings.isEmpty()) return

        updateVbos(beings)

        if (shader != null) {
            with(shader) {
                context.render.setShaderProgram(shader)
                putUniform("camera_projection", context.camera.projection)
                putUniform("camera_view", context.camera.view)
                putUniform("camera_position", camera.position)
                putUniform("light_position", light?.position ?: Vector3f(0f))
                putUniform("light_color", light?.color ?: Vector3f(0f))
                putUniform("time", context.time.elapsedInSeconds.toFloat())

                context.render.setTextures(textures)
            }
        } else {
            context.render.setShaderProgram(null)
        }

        context.render.setMesh(mesh)
        GL33.glDrawElementsInstanced(GL33.GL_TRIANGLES, mesh.getIndicesCount(), GL33.GL_UNSIGNED_INT, 0, beings.size)
    }

    override fun dispose() {
        super.dispose()
        GL30.glDeleteBuffers(transformationsVbo)
        GL30.glDeleteBuffers(texturesIndexesVbo)
        GL30.glDeleteBuffers(texturesScaleVbo)
    }

}