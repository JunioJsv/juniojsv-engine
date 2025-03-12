package juniojsv.engine.features.gui

import imgui.ImGui
import juniojsv.engine.features.context.WindowContext
import juniojsv.engine.features.entity.Light
import org.joml.Vector3f

interface MainLayoutListener {
    fun onGenerateObjects(count: Int, instanced: Boolean = false)
}

class MainLayout : IImGuiLayout {
    private var instanced = false
    private val objectsCount = intArrayOf(0)
    private val ambientColor = floatArrayOf(0f, 0f, 0f)
    private val listeners = mutableSetOf<MainLayoutListener>()

    fun addListener(listener: MainLayoutListener) {
        listeners.add(listener)
    }

    override fun setup(context: WindowContext) {
        context.render.state.light?.let {
            val color = it.color
            ambientColor[0] = color.x
            ambientColor[1] = color.y
            ambientColor[2] = color.z
        }
    }

    override fun render(context: WindowContext) {
        ImGui.begin("Scene Settings")

        ImGui.text("Number of Objects to Generate")
        ImGui.sliderInt("Objects", objectsCount, 1, 1024 * 5)

        if (ImGui.checkbox("Instanced", instanced)) {
            instanced = !instanced
            listeners.forEach {
                it.onGenerateObjects(objectsCount.first(), instanced)
            }
        }

        if (ImGui.button("Generate Objects")) {
            listeners.forEach {
                it.onGenerateObjects(objectsCount.first(), instanced)
            }
        }

        ImGui.spacing()
        ImGui.separator()
        ImGui.spacing()

        ImGui.text("Ambient Color")
        if (ImGui.colorEdit3("Color", ambientColor)) {
            val position = context.render.state.light?.position ?: Vector3f(0f)
            val color = Vector3f(ambientColor[0], ambientColor[1], ambientColor[2])
            context.render.setLight(Light(position, color))
        }

        ImGui.end()
    }
}