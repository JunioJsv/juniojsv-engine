package juniojsv.engine

class SkyBox(val cubeMap: CubeMap, val shader: Shader, var scale: Float = 500f) {
    fun scale(scale: Float) {
        this.scale = scale
    }
}