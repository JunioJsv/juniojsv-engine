@file:Suppress("UnstableApiUsage")

pluginManagement {
    val kotlinVersion = "2.1.20"
    plugins {
        id("com.android.application") version "8.9.0"
        id("com.android.library") version "8.9.0"
        kotlin("multiplatform") version kotlinVersion
        kotlin("jvm") version kotlinVersion
        kotlin("android") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.compose") version kotlinVersion
    }

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "juniojsv-engine"
include(":engine")
include(":example-common")
include(":example-jvm")
include(":example-android")
