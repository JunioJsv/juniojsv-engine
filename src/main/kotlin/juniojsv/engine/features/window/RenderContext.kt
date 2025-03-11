package juniojsv.engine.features.window

import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import juniojsv.engine.features.entity.Camera
import juniojsv.engine.features.entity.Light
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.texture.CubeMapTexture
import juniojsv.engine.features.texture.Texture
import juniojsv.engine.features.ui.IImGuiLayout
import juniojsv.engine.features.utils.Frustum
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class RenderContext(private val window: Window) : IRenderContext {
    private val currentCamera = Camera(Vector3f(0f), window)
    private lateinit var currentCameraProjection: Matrix4f
    private lateinit var currentCameraView: Matrix4f
    private lateinit var currentCameraFrustum: Frustum
    private var currentTexture: Texture? = null
    private var currentShaderProgram: ShadersProgram? = null
    private var currentMesh: Mesh? = null
    private var currentAmbientLight: Light? = null
    private var currentUi: IImGuiLayout? = null

    private var lastOnInitRender = 0.0
    private var delta = 0.0

    private var frames = 0

    private val textureUnits = listOf(
        GL13.GL_TEXTURE0, GL13.GL_TEXTURE1, GL13.GL_TEXTURE2, GL13.GL_TEXTURE3,
        GL13.GL_TEXTURE4, GL13.GL_TEXTURE5, GL13.GL_TEXTURE6, GL13.GL_TEXTURE7,
        GL13.GL_TEXTURE8, GL13.GL_TEXTURE9, GL13.GL_TEXTURE10, GL13.GL_TEXTURE11,
        GL13.GL_TEXTURE12, GL13.GL_TEXTURE13, GL13.GL_TEXTURE14, GL13.GL_TEXTURE15
    )

    override fun setCurrentShaderProgram(shader: ShadersProgram?) {
        if (shader != currentShaderProgram) {
            GL20.glUseProgram(shader?.id ?: 0)
            currentShaderProgram = shader
        }
    }

    override fun setCurrentTexture(texture: Texture?) {
        fun getTarget(texture: Texture): Int {
            return if (texture is CubeMapTexture) GL30.GL_TEXTURE_CUBE_MAP else GL11.GL_TEXTURE_2D
        }
        if (texture != currentTexture) {
            GL11.glBindTexture(
                texture?.let { getTarget(it) } ?: GL11.GL_TEXTURE_2D,
                texture?.id ?: 0
            )
            currentTexture = texture
        }
    }

    override fun setCurrentTextures(textures: Set<Texture>) {
        assert(textures.size <= textureUnits.size)
        textures.mapIndexed { index, texture ->
            val target = if (texture is CubeMapTexture) GL30.GL_TEXTURE_CUBE_MAP else GL11.GL_TEXTURE_2D
            GL30.glActiveTexture(textureUnits[index])
            GL30.glBindTexture(target, texture.id)
        }
        currentShaderProgram?.putUniform("textures", IntArray(textures.size) { it })
        GL30.glActiveTexture(textureUnits[0])
    }

    override fun setCurrentMesh(mesh: Mesh?) {
        if (mesh != currentMesh) {
            GL30.glBindVertexArray(mesh?.vao ?: 0)
            if (mesh != null) {
                GL20.glEnableVertexAttribArray(0)
                GL20.glEnableVertexAttribArray(1)
                GL20.glEnableVertexAttribArray(2)
            }
            currentMesh = mesh
        }
    }

    override fun setCurrentAmbientLight(light: Light) {
        currentAmbientLight = light
    }

    override fun setCurrentUi(layout: IImGuiLayout) {
        currentUi = layout
    }

    override fun getAmbientLight(): Light? = currentAmbientLight

    private fun getTime(): Double {
        return GLFW.glfwGetTime()
    }

    override fun getFramesCount(): Int = frames

    override fun getDelta(): Double = delta

    override fun getCamera(): Camera = currentCamera

    override fun getCameraProjection(): Matrix4f = currentCameraProjection

    override fun getCameraView(): Matrix4f = currentCameraView
    override fun getCameraFrustum(): Frustum = currentCameraFrustum

    fun onInitRender() {
        val onInit = getTime()
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        currentCameraProjection = currentCamera.projection()
        currentCameraView = currentCamera.view()
        val currentCameraProjectionView = Matrix4f()
        currentCameraProjection.mul(currentCameraView, currentCameraProjectionView)
        currentCameraFrustum = Frustum(currentCameraProjectionView)
        delta = getTime() - lastOnInitRender
        lastOnInitRender = onInit
    }

    fun onPostRender() {
        window.getImGuiGlfw().newFrame()
        ImGui.newFrame()
        currentUi?.render(this)
        ImGui.render()
        window.getImGuiGl3().renderDrawData(ImGui.getDrawData())

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            val context = GLFW.glfwGetCurrentContext()
            ImGui.updatePlatformWindows()
            ImGui.renderPlatformWindowsDefault()
            GLFW.glfwMakeContextCurrent(context)
        }

        GLFW.glfwSwapBuffers(window.getWindowContext().id)
        GLFW.glfwPollEvents()
        frames++
    }
}