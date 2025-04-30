plugins {
    id("com.android.application") apply false
    id("com.android.library") apply false
    kotlin("multiplatform") apply false
    kotlin("jvm") apply false
    kotlin("android") apply false
    kotlin("plugin.serialization") apply false
}

allprojects {
    group = "juniojsv.engine"
    version = "2.0.0"
}