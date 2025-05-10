package juniojsv.engine.features.utils

object ShaderConverter {
    fun fromGLtoGLES(source: String, isVertexShader: Boolean): String {
        val lines = source.lines().toMutableList()

        val versionIndex = lines.indexOfFirst { it.trim().startsWith("#version") }
        if (versionIndex >= 0) {
            lines[versionIndex] = "#version 300 es"
        }

        val layoutRegex = Regex("""layout\s*\(location\s*=\s*\d+\)\s*(in|out)\s""")
        for (i in lines.indices) {
            lines[i] = lines[i].replace(layoutRegex) {
                if (it.value.contains("in")) "in " else "out "
            }
        }

        if (!isVertexShader) {
            val precisionLine = "precision highp float;"
            val insertIndex = versionIndex + 1
            lines.add(insertIndex, precisionLine)
        }

        val fixedFloat = lines.joinToString("\n")
            .replace("""(?<=\d)\.(?!\d)""".toRegex(), ".0")

        return fixedFloat
    }
}