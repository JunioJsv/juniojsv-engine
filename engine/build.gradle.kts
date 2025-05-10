import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.get().compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }

    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets {
        all {
            languageSettings.enableLanguageFeature("BreakContinueInInlineLambdas")
        }

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
                implementation("de.javagl:obj:0.3.0")
                api("org.joml:joml:1.9.20")
                implementation("cz.advel.jbullet:jbullet:20101010-1")
                implementation("org.slf4j:slf4j-api:2.0.9")
            }
        }

        val jvmMain by getting {
            dependencies {
                api(project.dependencies.platform("org.lwjgl:lwjgl-bom:3.2.3"))
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

                implementation("ch.qos.logback:logback-classic:1.4.12")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("com.github.tony19:logback-android:3.0.0")
            }
        }
    }
}

android {
    namespace = group.toString()
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }

    sourceSets {
        named("main") {
            assets.srcDirs("src/commonMain/resources")
        }
    }
}