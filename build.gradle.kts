plugins {
    kotlin("jvm") apply false
}

subprojects {
    project.buildDir = File(rootProject.buildDir, project.name)
}

allprojects {
    group = "juniojsv.engine"

    repositories {
        mavenCentral()
    }
}