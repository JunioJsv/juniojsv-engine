package juniojsv.engine.entities

import juniojsv.engine.effects.Effect
import juniojsv.engine.essentials.Shell
import juniojsv.engine.essentials.Texture
import org.joml.Matrix4f

class SkyBox(val shell: Shell, val texture: Texture.CubeMap, val effect: Effect, override var scale: Float = 500f) : Entity() {
    fun transformation(): Matrix4f = Matrix4f().scale(scale)
}