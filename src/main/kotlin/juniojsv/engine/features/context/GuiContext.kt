package juniojsv.engine.features.context

import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import juniojsv.engine.features.gui.IImGuiLayout
import juniojsv.engine.features.window.Window
import org.lwjgl.glfw.GLFW

interface IGuiContext {
    var layout: IImGuiLayout?
}

class GuiContext(private val window: Window) : IGuiContext {
    override var layout: IImGuiLayout? = null

    fun onPostRender() {
        window.imGuiGlfw.newFrame()
        ImGui.newFrame()
        layout?.render(window.context)
        ImGui.render()
        window.imGuiGl3.renderDrawData(ImGui.getDrawData())

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            val defaultGlfwContext = GLFW.glfwGetCurrentContext()
            ImGui.updatePlatformWindows()
            ImGui.renderPlatformWindowsDefault()
            GLFW.glfwMakeContextCurrent(defaultGlfwContext)
        }
    }
}