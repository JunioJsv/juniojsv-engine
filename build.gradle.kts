plugins {
    kotlin("jvm") apply false
}

allprojects {
    group = "juniojsv.engine"

    repositories {
        mavenCentral()
        google()
    }
}