package juniojsv.engine.features.window

data class Resolution(val width: Int, val height: Int) {
    fun getAspectRatio(): Float = width.toFloat() / height.toFloat()
}