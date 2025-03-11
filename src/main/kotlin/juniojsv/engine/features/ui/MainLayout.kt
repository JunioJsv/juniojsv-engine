package juniojsv.engine.features.ui

import imgui.ImGui
import juniojsv.engine.features.entity.Light
import juniojsv.engine.features.window.IRenderContext
import org.joml.Vector3f

interface MainLayoutListener {
    fun onGenerateObjects(count: Int)
}

class MainLayout : IImGuiLayout {
    private val objectsCount = intArrayOf(0)
    private val ambientColor = floatArrayOf(0f, 0f, 0f)
    private val listeners = mutableSetOf<MainLayoutListener>()

    fun addListener(listener: MainLayoutListener) {
        listeners.add(listener)
    }

    override fun setup(context: IRenderContext) {
        context.getAmbientLight()?.let {
            val color = it.color
            ambientColor[0] = color.x
            ambientColor[1] = color.y
            ambientColor[2] = color.z
        }
    }

    override fun render(context: IRenderContext) {
        ImGui.begin("Scene Settings")

        ImGui.text("Number of Objects to Generate")
        ImGui.sliderInt("Objects", objectsCount, 1, 1024 * 5)

        if (ImGui.button("Generate Objects")) {
            listeners.forEach {
                it.onGenerateObjects(objectsCount.first())
            }
        }

        ImGui.separator()

        ImGui.text("Ambient Color")
        if (ImGui.colorEdit3("Color", ambientColor)) {
            val position = context.getAmbientLight()?.position ?: Vector3f(0f)
            val color = Vector3f(ambientColor[0], ambientColor[1], ambientColor[2])
            context.setCurrentAmbientLight(Light(position, color))
        }


        ImGui.end()
    }
}