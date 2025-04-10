plugins {
    application
    distribution
    kotlin("jvm")
}

version = "1.0.0"

application(Action {
    mainClassName = "juniojsv.engine.example.MainKt"
    applicationDefaultJvmArgs = arrayListOf(
        "-Xmx2g",
        "-XX:+AlwaysPreTouch"
    )
})

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(project(":engine"))
}