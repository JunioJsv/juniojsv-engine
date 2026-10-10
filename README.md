# juniojsv-engine

A multiplatform 3D game and rendering engine written in **Kotlin**, targeting desktop (**JVM**) and mobile (**Android**) using modern **OpenGL 3.3/4.3** and **OpenGL ES 3.0/3.2**.

[<p align="center"><img alt="juniojsv.engine demonstration" src="https://github.com/JunioJsv/juniojsv-bucket/blob/main/juniojsv.engine%202025-03-02%2023-06-38%20(1).gif?raw=true"/></p>](http://www.youtube.com/watch?v=az-pS03nu-U "")

---

## Overview

**juniojsv-engine** demonstrates how to build a unified, high-performance 3D engine in Kotlin Multiplatform (KMP) across desktop and mobile platforms without sacrificing platform-specific capabilities. 

Instead of treating desktop and mobile as separate codebases, the engine shares the core mathematical model, scene hierarchy, entity lifecycle, physics simulation, mesh handling, and multi-pass rendering pipeline in common code (`commonMain`). Low-level primitives—such as graphic contexts, off-heap memory allocation, resource streaming, and UI overlays—are abstracted using Kotlin's `expect`/`actual` declarations.

---

## Architecture

### Module Structure

```mermaid
graph TD
    subgraph Core Engine [":engine (Kotlin Multiplatform)"]
        engineCommon["engine:commonMain\n• Scene & Entity Model\n• RenderPipeline & FBO Management\n• JBullet Physics Loop\n• GL Abstraction & Math\n• Shader Preprocessor"]
        engineJvm["engine:jvmMain\n• LWJGL 3 (OpenGL 4.3)\n• GLFW Window & Events\n• ImGui Java Integration\n• Off-heap MemoryUtil"]
        engineAndroid["engine:androidMain\n• Android GLES 3.2\n• GLSurfaceView.Renderer\n• ShaderConverter (GLSL to GLES)\n• Direct NIO Buffers"]
        engineCommon -.-> engineJvm
        engineCommon -.-> engineAndroid
    end

    subgraph Showcase Modules
        exampleCommon[":example-common (KMP)\n• Shared MainScene\n• Asset Registry\n• Textures & Shaders"]
        exampleJvm[":example-jvm (JVM Application)\n• Desktop Entrypoint (Main.kt)\n• GLFW Event Polling\n• ImGui Control Panel"]
        exampleAndroid[":example-android (Android App)\n• MainActivity & GLSurfaceView\n• Jetpack Compose Virtual Gamepad"]
    end

    exampleCommon --> Core Engine
    exampleJvm --> exampleCommon
    exampleAndroid --> exampleCommon
```

### Module Responsibilities

| Module | Target | Responsibility |
|---|---|---|
| `:engine` | KMP (`jvm`, `android`) | Core engine architecture: multi-pass render pipeline, multi-threaded physics, memory abstraction, instancing, bounding shapes, and platform GL bindings. |
| `:example-common` | KMP (`jvm`, `android`) | Shared demonstration scene (`MainScene`), texture atlas loading, terrain generation, and dynamic mesh spawning. |
| `:example-jvm` | JVM (Application) | Desktop runtime: GLFW window management, keyboard/mouse first-person camera controller, and Dear ImGui debug interface. |
| `:example-android` | Android (Application) | Mobile runtime: Android Activity hosting `GLSurfaceView` with a Jetpack Compose touch gamepad overlay. |

---

## Core Engineering Features

### 1. Multi-Pass Render Pipeline

Rendering is orchestrated by [`RenderPipeline`](file:///F:/Projects/juniojsv-engine/engine/src/commonMain/kotlin/juniojsv/engine/features/render/RenderPipeline.kt) through isolated framebuffers (FBOs):

```mermaid
sequenceDiagram
    autonumber
    participant Loop as Window Loop
    participant RP as RenderPipeline
    participant ShadowFBO as Shadow FBO (8192x8192 Depth)
    participant SceneFBO as Scene FBO (Color + Depth)
    participant VelocityFBO as Velocity FBO (GL_RG16F)
    participant OverlayFBO as Overlay FBO (Debug)
    participant Composite as Fullscreen Viewport (window.frag)

    Loop->>RP: render(callbacks)
    RP->>ShadowFBO: 1. Shadow Pass (Front-face culling, Orthographic Sun Matrix)
    RP->>SceneFBO: 2. Scene Pass (Geometry, PCF Shadow Mapping, Dynamic Resolution Scale)
    RP->>VelocityFBO: 3. Velocity Pass (Per-instance screen-space motion vectors)
    RP->>OverlayFBO: 4. Overlay Pass (Depth blit from scene + wireframe debug draw)
    RP->>Composite: 5. Post-Processing & Composite
    Composite-->>Loop: Final Frame with Depth-Aware Motion Blur & Mini Debug Viewports
```

- **Directional Shadow Mapping**:
  - Rendered to an $8192 \times 8192$ depth-only buffer.
  - Front-face culling (`GL_FRONT`) during the shadow pass significantly mitigates shadow acne and peter panning.
  - 8-tap Percentage Closer Filtering (PCF) with screen-space jittering and slope-scaled depth bias in [`shadows.glsl`](file:///F:/Projects/juniojsv-engine/engine/src/commonMain/resources/shaders/default/shadows.glsl).
- **Velocity Buffer & Per-Object Motion Blur**:
  - The velocity pass renders per-entity motion vectors into a half-float framebuffer (`GL_RG16F`).
  - The composite shader [`window.frag`](file:///F:/Projects/juniojsv-engine/engine/src/commonMain/resources/shaders/window.frag) samples along each pixel's velocity vector (up to 32 samples) with a depth-difference rejection threshold to prevent motion blur from bleeding across silhouette edges.
- **Dynamic Resolution Scaling**:
  - Framebuffers can be dynamically scaled via `resolutionScale` without changing window or device screen resolution.

### 2. Multi-Threaded Physics with JBullet

- **Dedicated Thread Execution**: The physics simulation ([`PhysicsContext`](file:///F:/Projects/juniojsv-engine/engine/src/commonMain/kotlin/juniojsv/engine/features/context/PhysicsContext.kt)) runs in a background thread at a fixed 60 Hz tick (`FIXED_TIME_STEP = 1f / 60f`).
- **Thread Safety**: World mutations (adding/removing rigid bodies, listeners, collision objects) are marshalled across threads using a lock-free queue (`ConcurrentLinkedQueue`).
- **State Interpolation**: Visual transforms are interpolated (`onSimulationInterpolate`) between fixed simulation ticks to eliminate micro-stuttering during variable display refresh rates.
- **First-Person Character Controller**: [`PlayerController`](file:///F:/Projects/juniojsv-engine/engine/src/commonMain/kotlin/juniojsv/engine/features/entities/Player.kt) implements grounded checks using continuous convex sweep tests (`convexSweepTest`), ground friction, jump impulses, and air control attenuation.

### 3. Unified Bounding Shapes (`IBoundaryShape`)

Rather than maintaining separate representations for rendering, physics, and debugging, shapes implement [`IBoundaryShape`](file:///F:/Projects/juniojsv-engine/engine/src/commonMain/kotlin/juniojsv/engine/features/utils/IBoundaryShape.kt) (`BoundaryBox`, `BoundaryEllipsoid`):
1. **Frustum Culling**: Evaluates view-frustum plane equations (AABB, OBB with arbitrary orientation matrices, sphere, and ellipsoid) to drop off-screen draw calls.
2. **Physics Collision**: Instantiates matching JBullet shapes (`BoxShape`, `ConvexHullShape`).
3. **Debug Visuals**: Supplies transformation data to [`DebugShapeRender`](file:///F:/Projects/juniojsv-engine/engine/src/commonMain/kotlin/juniojsv/engine/features/render/DebugShapeRender.kt) for rendering line-strip wireframes.

### 4. Hardware Instanced Rendering

[`MultiEntityRender`](file:///F:/Projects/juniojsv-engine/engine/src/commonMain/kotlin/juniojsv/engine/features/render/MultiEntityRender.kt) renders thousands of identical meshes in a single draw call via `glDrawElementsInstanced`:
- Uses per-instance VBO attributes with divisor `1` (`glVertexAttribDivisor`):
  - Current Model Matrix (`mat4`)
  - Previous Model Matrix (`mat4`) — required for instantaneous velocity buffer computation
  - Texture Index (`int`)
  - UV Scale and UV Offset (`vec2`)

### 5. Cross-Platform Shader Transpilation

Shaders are authored once in desktop GLSL 330 with modular `#include` directives:
- [`Shader.kt`](file:///F:/Projects/juniojsv-engine/engine/src/commonMain/kotlin/juniojsv/engine/features/shader/Shader.kt) handles recursive `#include` resolution at load time.
- On Android, [`ShaderConverter.kt`](file:///F:/Projects/juniojsv-engine/engine/src/androidMain/kotlin/juniojsv/engine/features/utils/ShaderConverter.kt) automatically translates the shader to OpenGL ES 3.0:
  - Rewrites `#version 330` to `#version 300 es`.
  - Removes explicit `layout(location = ...)` input/output declarations unsupported in ES 3.0 fragment shaders.
  - Injects `precision highp float;`.
  - Normalizes floating-point numeric literals.

### 6. Skybox Luminance-Based Directional Light

Rather than relying on manually aligned sun positions, [`SkyBox`](file:///F:/Projects/juniojsv-engine/engine/src/commonMain/kotlin/juniojsv/engine/features/render/SkyBox.kt) and [`FileCubeMapTexture`](file:///F:/Projects/juniojsv-engine/engine/src/commonMain/kotlin/juniojsv/engine/features/textures/FileCubeMapTexture.kt) sample the pixels across all six cubemap faces to find the pixel of maximum luminance (`faceWithMaxPixelLuminance`). The engine automatically sets the primary ambient/directional light source's vector and color based on the brightest point in the skybox.

---

## Technology Stack

| Domain | Technologies |
|---|---|
| **Language & Tooling** | Kotlin 2.1.20 (Multiplatform), Gradle 8.12, Java 17 |
| **Desktop Graphics & Windowing** | LWJGL 3.2.3 (OpenGL 4.3, GLFW) |
| **Mobile Graphics & UI** | Android OpenGL ES 3.2 (`GLSurfaceView`), Jetpack Compose (BOM 2025.02.00) |
| **Physics Simulation** | JBullet (Java port of Bullet Physics) |
| **Math & Matrix Operations** | JOML 1.9.20 (Java OpenGL Math Library) |
| **Mesh Loading** | `de.javagl:obj` 0.3.0 (Wavefront `.obj` parser) |
| **Desktop GUI / Debugging** | Dear ImGui via `imgui-java` 1.81.0 (LWJGL3 / GLFW binding) |
| **Logging** | SLF4J 2.0.9 + Logback Classic (JVM) / Logback Android (Mobile) |

---

## Environment Prerequisites

- **Java Development Kit (JDK)**: Version 17 or higher.
- **Desktop Target**: Windows, Linux, or macOS x64/arm64 (LWJGL and ImGui natives are detected automatically from the host OS, or configurable via `-PdesktopNatives`).
- **Android Target**: Android SDK with API Level 34 (`compileSdk = 34`, `minSdk = 24`) and `ANDROID_HOME` configured.

---

## Building and Running

### 1. Run the Desktop Demo (JVM)

To launch the desktop demonstration with GLFW and ImGui (automatically downloads the correct native binaries for your operating system):

```bash
# Windows
.\gradlew.bat :example-jvm:run

# Linux / macOS
./gradlew :example-jvm:run

# Optionally specify natives or bundle all (windows, linux, macos, or all):
./gradlew :example-jvm:run -PdesktopNatives=all
```

### 2. Build the Android Application

To assemble the debug APK:

```bash
# Windows
.\gradlew.bat :example-android:assembleDebug

# Linux / macOS
./gradlew :example-android:assembleDebug
```

The APK will be generated at `example-android/build/outputs/apk/debug/example-android-debug.apk`.

### 3. Compile the Multiplatform Engine Library

To verify compilation across common and platform targets:

```bash
.\gradlew.bat :engine:assemble
```

---

## Controls

### Desktop (JVM)

| Input | Action |
|---|---|
| **Left Click** | Toggle camera capture mode (locks/unlocks mouse cursor) |
| **Mouse Look** | Rotate active camera (Yaw / Pitch) |
| **W / A / S / D** | Move Forward / Left / Backward / Right |
| **Space** | Jump (Player mode) / Move Up (Free-look mode) |
| **Left Shift** | Move Down (Free-look mode) |
| **C** | Cycle between player camera and free-look camera |
| **Esc** | Close application |
| **ImGui Panel** | Configure active skybox, entity spawn count, instancing mode, physics speed, shadows toggle, and debug viewports in real time |

### Android

| Input | Action |
|---|---|
| **Virtual Joystick** (Left) | Directional movement (forward, backward, strafe) |
| **Touch Drag** (Right) | Camera look / rotation |
| **Button A** | Switch camera perspective |
| **Button B** | Jump |
| **Button X** | Cycle to next skybox |
| **Button Y** | Spawn 100 random physics entities |

---

## Known Limitations

- **Platform Natives**: Dynamic host OS native resolution is supported for Windows, Linux, and macOS. When building distributions or running on non-standard architectures, use `-PdesktopNatives=<platform|all>` to explicitly choose or bundle the target native binaries.
- **Automated Tests**: The project does not currently contain an automated unit test suite (`src/commonTest`); functional and performance validation is handled via the sample applications.
- **Kotlin Compiler Flags**: Uses internal compiler flags (`-Xexpect-actual-classes` and language feature `BreakContinueInInlineLambdas`) configured in the root build scripts.
- **Library Distribution**: Artifact publishing is targeted to GitHub Packages (`maven.pkg.github.com/JunioJsv/juniojsv-engine`) via `.github/workflows/publish.yaml`.

---

## License

This project is licensed under the [MIT License](LICENSE). See [`LICENSE`](LICENSE) for details.
