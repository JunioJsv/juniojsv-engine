package juniojsv.engine.features.gui

import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import juniojsv.engine.features.window.JvmWindow
import juniojsv.engine.features.window.PlatformWindow
import org.lwjgl.glfw.GLFW

abstract class ImGuiLayout : GuiLayout() {
    override fun render(window: PlatformWindow) {
        require(window is JvmWindow)
        window.imGuiGlfw.newFrame()
        ImGui.newFrame()
        render(window.context)
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