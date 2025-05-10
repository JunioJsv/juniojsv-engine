plugins {
    application
    distribution
    kotlin("jvm")
}

application {
    mainClass.set("juniojsv.engine.example.MainKt")
    applicationDefaultJvmArgs = arrayListOf(
        "-Xmx2g",
        "-XX:+AlwaysPreTouch"
    )
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(project(":example-common"))
}