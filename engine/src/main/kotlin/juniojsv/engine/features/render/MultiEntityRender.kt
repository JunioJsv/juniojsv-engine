package juniojsv.engine.features.render

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.context.IWindowContextListener
import juniojsv.engine.features.entity.Entity
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.texture.Texture
import juniojsv.engine.features.texture.Texture.Companion.bind
import juniojsv.engine.features.utils.Constants.BYTES_PER_FLOAT
import juniojsv.engine.features.utils.Constants.FLOAT_BYTE_SIZE
import juniojsv.engine.features.utils.Constants.FLOAT_SIZE
import juniojsv.engine.features.utils.Constants.INT_BYTE_SIZE
import juniojsv.engine.features.utils.Constants.INT_SIZE
import juniojsv.engine.features.utils.Constants.MAT4_BYTE_SIZE
import juniojsv.engine.features.utils.Constants.VEC4_SIZE
import juniojsv.engine.features.utils.RenderTarget
import org.joml.Vector3f
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL33
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ConcurrentLinkedQueue

class MultiEntityRender(
    mesh: Mesh,
    shader: ShadersProgram,
    isDebuggable: Boolean = true,
    isFrustumCullingEnabled: Boolean = true,
    private val isPhysicsEnabled: Boolean = true,
    isShaderOverridable: Boolean = true,
    private val glMode: Int = GL30.GL_TRIANGLES,
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
        private const val INSTANCE_DATA_START_LOCATION = 3
        private const val VBO_COUNT = 4

        /** Uses locations 3, 4, 5, 6 */
        private const val LOCATION_MODEL_MATRIX = INSTANCE_DATA_START_LOCATION
        private const val MODEL_VBO_INDEX = 0

        /** Uses locations 7, 8, 9, 10 */
        private const val LOCATION_PREVIOUS_MODEL_MATRIX = LOCATION_MODEL_MATRIX + VEC4_SIZE
        private const val PREVIOUS_MODEL_VBO_INDEX = MODEL_VBO_INDEX + 1

        /** Uses location 11 */
        private const val LOCATION_TEXTURE_INDEX = LOCATION_PREVIOUS_MODEL_MATRIX + VEC4_SIZE
        private const val TEXTURE_VBO_INDEX = PREVIOUS_MODEL_VBO_INDEX + 1

        /** Uses location 12 */
        private const val LOCATION_TEXTURE_SCALE = LOCATION_TEXTURE_INDEX + FLOAT_SIZE
        private const val TEXTURE_SCALE_VBO_INDEX = TEXTURE_VBO_INDEX + 1

        private const val INSTANCE_DIVISOR = 1 // Update attribute per instance

        private val logger = LoggerFactory.getLogger(MultiEntityRender::class.java)
    }

    private val vbos = IntArray(VBO_COUNT)

    constructor(
        mesh: Mesh,
        shader: ShadersProgram,
        entities: List<Entity>,
        isDebuggable: Boolean = true,
        isFrustumCullingEnabled: Boolean = true,
        isPhysicsEnabled: Boolean = true,
        isShaderOverridable: Boolean = true
    ) : this(mesh, shader, isDebuggable, isFrustumCullingEnabled, isPhysicsEnabled, isShaderOverridable) {
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

    init {
        GL30.glBindVertexArray(mesh.vao)

        vbos[MODEL_VBO_INDEX] = GL30.glGenBuffers().also { vbo ->
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo)

            for (i in 0 until VEC4_SIZE) {
                val location = LOCATION_MODEL_MATRIX + i
                GL30.glVertexAttribPointer(
                    location,
                    VEC4_SIZE,
                    GL30.GL_FLOAT,
                    false,
                    MAT4_BYTE_SIZE,
                    (i * VEC4_SIZE * BYTES_PER_FLOAT).toLong()
                )
                GL30.glEnableVertexAttribArray(location)
                GL33.glVertexAttribDivisor(location, INSTANCE_DIVISOR)
            }
        }

        vbos[PREVIOUS_MODEL_VBO_INDEX] = GL30.glGenBuffers().also { vbo ->
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo)

            for (i in 0 until VEC4_SIZE) {
                val location = LOCATION_PREVIOUS_MODEL_MATRIX + i
                GL30.glVertexAttribPointer(
                    location,
                    VEC4_SIZE,
                    GL30.GL_FLOAT,
                    false,
                    MAT4_BYTE_SIZE,
                    (i * VEC4_SIZE * BYTES_PER_FLOAT).toLong()
                )
                GL30.glEnableVertexAttribArray(location)
                GL33.glVertexAttribDivisor(location, INSTANCE_DIVISOR)
            }
        }

        vbos[TEXTURE_VBO_INDEX] = GL30.glGenBuffers().also { vbo ->
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo)

            val location = LOCATION_TEXTURE_INDEX
            GL30.glVertexAttribIPointer(location, INT_SIZE, GL30.GL_INT, 0, 0)
            GL30.glEnableVertexAttribArray(location)
            GL33.glVertexAttribDivisor(location, INSTANCE_DIVISOR)
        }

        vbos[TEXTURE_SCALE_VBO_INDEX] = GL30.glGenBuffers().also { vbo ->
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo)

            val location = LOCATION_TEXTURE_SCALE
            GL30.glVertexAttribPointer(location, FLOAT_SIZE, GL30.GL_FLOAT, false, 0, 0)
            GL30.glEnableVertexAttribArray(location)
            GL33.glVertexAttribDivisor(location, INSTANCE_DIVISOR)
        }

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0)
        GL30.glBindVertexArray(0)
    }

    private fun updateVbos(entities: List<Entity>) {
        updateModelVbo(entities)
        updatePreviousModelVbo(entities)
        updateTextureIndexVbo(entities)
        updateTextureScaleVbo(entities)
        GL30.glBindVertexArray(0)
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0)
    }


    private fun updateTextureIndexVbo(entities: List<Entity>) {
        val bytes = (entities.size * INT_BYTE_SIZE).toLong()
        if (bytes == 0L) return
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbos[TEXTURE_VBO_INDEX])
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, bytes, GL30.GL_STREAM_DRAW)

        var buffer: ByteBuffer? = null

        try {
            buffer = GL30.glMapBufferRange(
                GL30.GL_ARRAY_BUFFER,
                0,
                bytes,
                GL30.GL_MAP_WRITE_BIT or GL30.GL_MAP_INVALIDATE_BUFFER_BIT
            )
            if (buffer == null) return
            buffer.order(ByteOrder.nativeOrder())

            for (entity in entities) {
                val material = entity.material
                val textureIndex = textures.indexOfFirst { material?.texture?.id == it.id }
                buffer.putInt(textureIndex)
            }
        } catch (e: Exception) {
            logger.error("Error updating texture index VBO: ${e.message}")
        } finally {
            if (buffer != null) GL30.glUnmapBuffer(GL30.GL_ARRAY_BUFFER)
        }

    }

    private fun updateTextureScaleVbo(entities: List<Entity>) {
        val bytes = (entities.size * FLOAT_BYTE_SIZE).toLong()
        if (bytes == 0L) return
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbos[TEXTURE_SCALE_VBO_INDEX])
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, bytes, GL30.GL_STREAM_DRAW)

        var buffer: ByteBuffer? = null

        try {
            buffer = GL30.glMapBufferRange(
                GL30.GL_ARRAY_BUFFER,
                0,
                bytes,
                GL30.GL_MAP_WRITE_BIT or GL30.GL_MAP_INVALIDATE_BUFFER_BIT
            )
            if (buffer == null) return
            buffer.order(ByteOrder.nativeOrder())

            for (entity in entities) {
                val material = entity.material
                buffer.putFloat(material?.scale ?: 1f)
            }
        } catch (e: Exception) {
            logger.error("Error updating texture scale VBO: ${e.message}")
        } finally {
            if (buffer != null) GL30.glUnmapBuffer(GL30.GL_ARRAY_BUFFER)
        }
    }

    private fun updateModelVbo(entities: List<Entity>) {
        val bytes = (entities.size * MAT4_BYTE_SIZE).toLong()
        if (bytes == 0L) return
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbos[MODEL_VBO_INDEX])
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, bytes, GL30.GL_STREAM_DRAW)

        var buffer: ByteBuffer? = null

        try {
            buffer = GL30.glMapBufferRange(
                GL30.GL_ARRAY_BUFFER,
                0,
                bytes,
                GL30.GL_MAP_WRITE_BIT or GL30.GL_MAP_INVALIDATE_BUFFER_BIT
            )
            if (buffer == null) return
            buffer.order(ByteOrder.nativeOrder())

            for ((index, entity) in entities.withIndex()) {
                val transformationMatrix = entity.transform.transformation()
                val position = index * MAT4_BYTE_SIZE
                transformationMatrix.get(position, buffer)
            }
        } catch (e: Exception) {
            logger.error("Error updating model VBO: ${e.message}")
        } finally {
            if (buffer != null) GL30.glUnmapBuffer(GL30.GL_ARRAY_BUFFER)
        }
    }

    private fun updatePreviousModelVbo(entities: List<Entity>) {
        val bytes = (entities.size * MAT4_BYTE_SIZE).toLong()
        if (bytes == 0L) return
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbos[PREVIOUS_MODEL_VBO_INDEX])
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, bytes, GL30.GL_STREAM_DRAW)

        var buffer: ByteBuffer? = null

        try {
            buffer = GL30.glMapBufferRange(
                GL30.GL_ARRAY_BUFFER,
                0,
                bytes,
                GL30.GL_MAP_WRITE_BIT or GL30.GL_MAP_INVALIDATE_BUFFER_BIT
            )
            if (buffer == null) return
            buffer.order(ByteOrder.nativeOrder())

            for ((index, entity) in entities.withIndex()) {
                val transformationMatrix = entity.transform.previous.transformation()
                val position = index * MAT4_BYTE_SIZE
                transformationMatrix.get(position, buffer)
            }
        } catch (e: Exception) {
            logger.error("Error updating previous model VBO: ${e.message}")
        } finally {
            if (buffer != null) GL30.glUnmapBuffer(GL30.GL_ARRAY_BUFFER)
        }
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
            uniforms["uLightPosition"] = light?.origin ?: Vector3f(0f)
            uniforms["uLightColor"] = light?.color ?: Vector3f(0f)
            uniforms["uTime"] = context.time.elapsedInSeconds.toFloat()
            it.applyUniforms()
        }

        mesh.bind()
        GL33.glDrawElementsInstanced(glMode, mesh.getIndicesCount(), GL33.GL_UNSIGNED_INT, 0, entities.size)
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
        GL30.glDeleteBuffers(vbos)
        context.removeListener(this)
        disposeEntities()
    }
}