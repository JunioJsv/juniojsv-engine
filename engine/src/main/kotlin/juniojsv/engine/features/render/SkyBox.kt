package juniojsv.engine.features.render

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.entity.Light
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.texture.FileCubeMapTexture
import juniojsv.engine.features.utils.RenderTarget
import juniojsv.engine.features.utils.Scale
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL30

class SkyBox(
    mesh: Mesh,
    var texture: FileCubeMapTexture,
    shader: ShadersProgram,
    var scale: Float = Scale.KILOMETER.length(1f),
    isShaderOverridable: Boolean = true,
    isEnabled: Boolean = true
) : MeshRenderer(RenderTarget.SINGLE, mesh, shader, false, isShaderOverridable, false, isEnabled) {
    private fun transformation(): Matrix4f = Matrix4f().scale(scale)

    private fun getAmbientLightColor(): Vector3f = texture.faceWithMaxPixelLuminance.value.color

    private fun getMaxPixelLuminancePosition(): Vector3f {
        val faceWithMaxPixelLuminance = texture.faceWithMaxPixelLuminance
        val face = faceWithMaxPixelLuminance.index
        val (x, y) = faceWithMaxPixelLuminance.value.x to faceWithMaxPixelLuminance.value.y
        val (width, height) = texture.width to texture.height

        val uv = Vector3f(
            (x + 0.5f) / width * 2f - 1f,
            (y + 0.5f) / height * 2f - 1f,
            1f
        )

        return when (face) {
            0 -> Vector3f(1f, -uv.y, -uv.x)
            1 -> Vector3f(-1f, -uv.y, uv.x)
            2 -> Vector3f(uv.x, 1f, uv.y)
            3 -> Vector3f(uv.x, -1f, -uv.y)
            4 -> Vector3f(uv.x, -uv.y, 1f)
            5 -> Vector3f(-uv.x, -uv.y, -1f)
            else -> Vector3f(0f)
        }.mul(scale)
    }

    fun getAmbientLight() = Light(getMaxPixelLuminancePosition(), getAmbientLightColor())

    override fun render(context: IWindowContext) {
        super.render(context)

        if (!isEnabled) return

        val transformation = transformation()
        GL30.glDisable(GL30.GL_DEPTH_TEST)

        texture.bind()

        getShader().also {
            it.bind()
            uniforms["uModel"] = transformation
            uniforms["uPreviousModel"] = transformation
            uniforms["uProjection"] = context.camera.projection
            uniforms["uView"] = context.camera.view
            uniforms["uPreviousProjection"] = context.camera.previousProjection
            uniforms["uPreviousView"] = context.camera.previousView
            uniforms["uTime"] = context.time.current.toFloat()
            it.applyUniforms()
        }
        mesh.bind()
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, mesh.getIndicesCount())

        GL30.glEnable(GL30.GL_DEPTH_TEST)
    }
}