package juniojsv.engine.features.window

data class Resolution(val width: Int, val height: Int) {
    fun getAspectRatio(): Float = width.toFloat() / height.toFloat()
    fun withResolutionScale(scale: Float): Resolution =
        Resolution((width * scale).toInt(), (height * scale).toInt())
}