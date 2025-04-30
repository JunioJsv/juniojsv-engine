import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.library")
    kotlin("multiplatform")
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
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
                api(project(":engine"))
            }
        }
        val jvmMain by getting {
            dependencies {}
        }
        val androidMain by getting {
            dependencies {}
        }
    }
}

android {
    namespace = "$group.example"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }

    sourceSets {
        named("main") {
            assets.srcDirs("src/commonMain/resources", "src/androidMain/resources")
        }
    }
}