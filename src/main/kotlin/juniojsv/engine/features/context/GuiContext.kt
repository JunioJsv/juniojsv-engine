package juniojsv.engine.features.context

import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import juniojsv.engine.features.ui.IImGuiLayout
import juniojsv.engine.features.window.Window
import org.lwjgl.glfw.GLFW

class GuiContext(private val window: Window) {
    var layout: IImGuiLayout? = null

    fun onPostRender(context: WindowContext) {
        window.getImGuiGlfw().newFrame()
        ImGui.newFrame()
        layout?.render(context)
        ImGui.render()
        window.getImGuiGl3().renderDrawData(ImGui.getDrawData())

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            val glfwContext = GLFW.glfwGetCurrentContext()
            ImGui.updatePlatformWindows()
            ImGui.renderPlatformWindowsDefault()
            GLFW.glfwMakeContextCurrent(glfwContext)
        }
    }
}