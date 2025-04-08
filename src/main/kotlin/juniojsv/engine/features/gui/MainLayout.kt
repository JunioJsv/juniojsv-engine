package juniojsv.engine.features.gui

import imgui.ImGui
import imgui.flag.ImGuiComboFlags
import imgui.flag.ImGuiWindowFlags
import juniojsv.engine.Config
import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.entity.Light
import org.joml.Vector3f

interface MainLayoutListener {
    fun didResolutionScaleChanged(scale: Float) {}
}

interface MainLayoutCallbacks {
    fun getSkyboxCount(): Int
    fun setSkybox(context: IWindowContext, index: Int)
    fun onGenerateObjects(count: Int, instanced: Boolean = false)
}

class MainLayout(private val callbacks: MainLayoutCallbacks) : IImGuiLayout {
    private var instanced = false
    private val objectsCount = intArrayOf(0)
    private val resolutionScale = floatArrayOf(1f)
    private val motionBlur = floatArrayOf(.5f)
    private val ambientColor = floatArrayOf(0f, 0f, 0f)
    private val listeners = mutableSetOf<MainLayoutListener>()
    private val physicsSpeed = floatArrayOf(0f)
    private val skyboxIndex = intArrayOf(0)

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

        ImGui.text("Select Skybox")
        val skyboxCount = callbacks.getSkyboxCount()
        if (skyboxCount > 0) {
            val skyboxNames = (0 until skyboxCount).map { "Skybox ${it + 1}" }.toTypedArray()
            val currentSkyboxName = skyboxNames.getOrNull(skyboxIndex.first()) ?: ""
            if (ImGui.beginCombo("Skybox", currentSkyboxName, ImGuiComboFlags.NoArrowButton)) {
                for (i in skyboxNames.indices) {
                    if (ImGui.selectable(skyboxNames[i], skyboxIndex.first() == i)) {
                        skyboxIndex[0] = i
                        callbacks.setSkybox(context, skyboxIndex.first())
                    }
                }
                ImGui.endCombo()
            }
        }

        ImGui.text("Number of Objects to Generate")
        ImGui.sliderInt("Objects", objectsCount, 1, 1024 * 5)

        if (ImGui.checkbox("Instanced", instanced)) {
            instanced = !instanced
            callbacks.onGenerateObjects(objectsCount.first(), instanced)
        }

        if (ImGui.button("Generate Objects")) {
            callbacks.onGenerateObjects(objectsCount.first(), instanced)
        }

        ImGui.text("Physics Speed")
        if (ImGui.sliderFloat("Speed", physicsSpeed, 0f, 3f)) {
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
