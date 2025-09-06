# juniojsv-engine

My OpenGL multiplatform engine written in Kotlin — Supports JVM desktop and Android.

[![](https://jitpack.io/v/JunioJsv/juniojsv-engine.svg)](https://jitpack.io/#JunioJsv/juniojsv-engine)

[<p align="center"><img alt="juniojsv.engine" src="https://github.com/JunioJsv/juniojsv-bucket/blob/main/juniojsv.engine%202025-03-02%2023-06-38%20(1).gif?raw=true"/></p>](http://www.youtube.com/watch?v=az-pS03nu-U "")

## 🔥 Features

- ⚙️ **Kotlin Multiplatform** — shared engine code for JVM (desktop) and Android
- 🎮 **Virtual Gamepad** — touch controls for Android builds
- 🎥 **Multi-Camera System** — switch between player and free-look cameras
- 💡 **Lighting System**
  - Ambient light from skybox
  - Real-time shadows with reduced acne/peter panning
- 🧍 **Entity System** — unified rendering and physics with JBullet integration
- 🌫️ **Motion Blur** — based on entity velocity
- 🔲 **Frustum Culling** — improves performance by skipping off-screen entities
- 🖼️ **Texture Support**
  - 2D textures
  - Cubemaps (skybox)
  - Texture atlas
- 🧭 **Wavefront (.obj) Loader** — static mesh support
- 🧰 **Render Pipeline** — modular and extensible rendering architecture
- 🧪 **Debug Tools** — ImGui integration and debug visualizations

## 📦 How to Use

### 1. Add JitPack Repository

In your **root `settings.gradle`**, add the JitPack repository at the end of the `repositories` block:

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### 2. Add Dependency

In your **module `build.gradle`**:

```gradle
dependencies {
    implementation 'com.github.JunioJsv:juniojsv-engine:v3.0.0'
}
```