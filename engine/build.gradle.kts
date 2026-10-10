import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("maven-publish")
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
        publishLibraryVariants("release")
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
                val imGuiVersion = "1.81.0"
                api("io.github.spair:imgui-java-binding:$imGuiVersion")
                implementation("io.github.spair:imgui-java-lwjgl3:$imGuiVersion")

                val osName = System.getProperty("os.name").lowercase()
                val detectedOs = when {
                    osName.contains("win") -> "windows"
                    osName.contains("mac") || osName.contains("darwin") -> "macos"
                    osName.contains("linux") || osName.contains("unix") -> "linux"
                    else -> "windows"
                }
                val desktopNatives = (findProperty("desktopNatives") as? String)?.lowercase() ?: detectedOs
                val platforms = if (desktopNatives == "all") {
                    listOf("windows", "linux", "macos")
                } else {
                    listOf(desktopNatives)
                }

                for (platform in platforms) {
                    runtimeOnly("org.lwjgl:lwjgl::natives-$platform")
                    runtimeOnly("org.lwjgl:lwjgl-glfw::natives-$platform")
                    runtimeOnly("org.lwjgl:lwjgl-opengl::natives-$platform")
                    runtimeOnly("io.github.spair:imgui-java-natives-$platform:$imGuiVersion")
                }

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

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/JunioJsv/juniojsv-engine")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}