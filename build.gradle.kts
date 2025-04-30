plugins {
    kotlin("jvm") apply false
}

allprojects {
    group = "juniojsv.engine"
    version = "2.0.0"

    repositories {
        mavenCentral()
        google()
    }
}