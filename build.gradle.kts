plugins {
    application
    distribution
    id("org.jetbrains.kotlin.jvm") version ("1.3.61")
}

group = "juniojsv.engine"
version = "1.0.0"

repositories {
    mavenCentral()
}

application {
    mainClassName = "juniojsv.engine.MainKt"
}

dependencies {
    implementation("de.javagl:obj:0.3.0")
    implementation("org.joml:joml:1.9.20")
    implementation(platform("org.lwjgl:lwjgl-bom:3.2.3"))
    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-opengl")
    runtimeOnly("org.lwjgl:lwjgl::natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-glfw::natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-opengl::natives-windows")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}