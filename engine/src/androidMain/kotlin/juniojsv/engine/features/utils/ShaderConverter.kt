package juniojsv.engine.features.utils

object ShaderConverter {
    fun fromGLtoGLES(source: String, isVertexShader: Boolean): String {
        val lines = source.lines().toMutableList()

        // Troca #version 330 por #version 300 es
        val versionIndex = lines.indexOfFirst { it.trim().startsWith("#version") }
        if (versionIndex >= 0) {
            lines[versionIndex] = "#version 300 es"
        }

        // Remove layouts (ex: layout (location = 0) in ...)
        val layoutRegex = Regex("""layout\s*\(location\s*=\s*\d+\)\s*(in|out)\s""")
        for (i in lines.indices) {
            lines[i] = lines[i].replace(layoutRegex) {
                if (it.value.contains("in")) "in " else "out "
            }
        }

        // Adiciona precisão no fragment shader
        if (!isVertexShader) {
            val precisionLine = "precision highp float;"
            val insertIndex = versionIndex + 1
            lines.add(insertIndex, precisionLine)
        }

        // Corrige floats sem .0 (opcional)
        val fixedFloat = lines.joinToString("\n")
            .replace("""(?<=\d)\.(?!\d)""".toRegex(), ".0") // transforma "1." em "1.0"

        return fixedFloat
    }
}