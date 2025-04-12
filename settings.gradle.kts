pluginManagement {
    val kotlinVersion = "2.1.20"
    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version  kotlinVersion
    }
}
rootProject.name = "juniojsv-engine"
include(":engine")
include(":example")