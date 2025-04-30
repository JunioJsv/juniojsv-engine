import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation("de.javagl:obj:0.3.0")

    api("org.joml:joml:1.9.20")

    api(platform("org.lwjgl:lwjgl-bom:3.2.3"))
    api("org.lwjgl:lwjgl")
    api("org.lwjgl:lwjgl-glfw")
    api("org.lwjgl:lwjgl-opengl")
    runtimeOnly("org.lwjgl:lwjgl::natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-glfw::natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-opengl::natives-windows")

    val imGuiVersion = "1.81.0"
    api("io.github.spair:imgui-java-binding:$imGuiVersion")
    implementation("io.github.spair:imgui-java-lwjgl3:$imGuiVersion")
    runtimeOnly("io.github.spair:imgui-java-natives-windows:$imGuiVersion")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.12")

    implementation("cz.advel.jbullet:jbullet:20101010-1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    freeCompilerArgs.set(listOf("-Xnon-local-break-continue"))
}