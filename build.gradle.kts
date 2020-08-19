plugins {
    id("org.jetbrains.kotlin.jvm") version ("1.3.61")
}

group = "juniojsv.engine"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
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

tasks.jar {
    manifest {
        attributes["Main-Class"] = "juniojsv.engine.MainKt"
    }
    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    })
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}