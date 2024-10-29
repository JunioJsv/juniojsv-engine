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
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class RenderContext(private val window: Window) : IRenderContext {
    private val currentCamera = Camera(Vector3f(0f), window)
    private var currentTexture: Texture? = null
    private var currentShaderProgram: ShadersProgram? = null
    private var currentMesh: Mesh? = null
    private var currentAmbientLight: Light? = null
    private var currentUi: IImGuiLayout? = null

    private var lastOnInitDraw = 0.0
    private var delta = 0.0

    private var frames = 0

    override fun setCurrentShaderProgram(shader: ShadersProgram?) {
        if (shader != null) {
            if (shader.id != currentShaderProgram?.id) {
                GL20.glUseProgram(shader.id)
                currentShaderProgram = shader
            }
        } else {
            GL20.glUseProgram(0)
            currentShaderProgram = null
        }
    }

    override fun setCurrentTexture(texture: Texture?) {
        fun getTarget(texture: Texture): Int {
            return if (texture is CubeMapTexture) GL30.GL_TEXTURE_CUBE_MAP else GL11.GL_TEXTURE_2D
        }
        if (texture != null) {
            if (texture.id != currentTexture?.id) {
                GL11.glBindTexture(
                    getTarget(texture),
                    texture.id
                )
                currentTexture = texture
            }
        } else {
            currentTexture?.let {
                GL11.glBindTexture(getTarget(it), 0)
                currentTexture = null
            }
        }
    }

    override fun setCurrentMesh(mesh: Mesh?) {
        if (mesh != null) {
            if (mesh.id != currentMesh?.id) {
                GL30.glBindVertexArray(mesh.id)
                GL20.glEnableVertexAttribArray(0)
                GL20.glEnableVertexAttribArray(1)
                GL20.glEnableVertexAttribArray(2)
                currentMesh = mesh
            }
        } else {
            GL30.glBindVertexArray(0)
            currentMesh = null
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

    fun onInitDraw() {
        val onInit = getTime()
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        delta = getTime() - lastOnInitDraw
        lastOnInitDraw = onInit
    }

    fun onPostDraw() {
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