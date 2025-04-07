package juniojsv.engine.features.gui

import imgui.ImGui
import imgui.flag.ImGuiWindowFlags
import juniojsv.engine.Config
import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.entity.Light
import org.joml.Vector3f

interface MainLayoutListener {
    fun onGenerateObjects(count: Int, instanced: Boolean = false) {}
    fun didResolutionScaleChanged(scale: Float) {}
}

class MainLayout : IImGuiLayout {
    private var instanced = false
    private val objectsCount = intArrayOf(0)
    private val resolutionScale = floatArrayOf(1f)
    private val motionBlur = floatArrayOf(.5f)
    private val ambientColor = floatArrayOf(0f, 0f, 0f)
    private val listeners = mutableSetOf<MainLayoutListener>()
    private val physicsSpeed = floatArrayOf(0f)

    fun addListener(listener: MainLayoutListener) {
        listeners.add(listener)
    }

    override fun setup(context: IWindowContext) {
        context.render.ambientLight?.let {
            val color = it.color
            ambientColor[0] = color.x
            ambientColor[1] = color.y
            ambientColor[2] = color.z
        }
        physicsSpeed[0] = context.physics.speed
        motionBlur[0] = context.render.motionBlur
    }

    override fun render(context: IWindowContext) {
        ImGui.begin(
            "Scene Settings", ImGuiWindowFlags.NoResize or ImGuiWindowFlags.AlwaysAutoResize
        )

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

        ImGui.text("Physics Speed")
        if (ImGui.sliderFloat("Speed", physicsSpeed, 0f, 10f)) {
            context.physics.speed = physicsSpeed.first()
        }

        ImGui.spacing()
        ImGui.separator()
        ImGui.spacing()

        if (ImGui.checkbox("Show Debug Objects", Config.isDebug)) {
            Config.isDebug = !Config.isDebug
        }

        ImGui.text("Ambient Color")
        if (ImGui.colorEdit3("Color", ambientColor)) {
            val position = context.render.ambientLight?.position ?: Vector3f(0f)
            val color = Vector3f(ambientColor[0], ambientColor[1], ambientColor[2])
            context.render.ambientLight = Light(position, color)
        }

        ImGui.text("Resolution Scale")
        if (ImGui.sliderFloat("Scale", resolutionScale, 0.1f, 1f)) {
            context.render.resolutionScale = resolutionScale.first()
            listeners.forEach {
                it.didResolutionScaleChanged(resolutionScale.first())
            }
        }

        ImGui.text("Motion Blur")
        if (ImGui.sliderFloat("Strength", motionBlur, 0f, .9f)) {
            context.render.motionBlur = motionBlur.first()
        }

        ImGui.end()
    }
}