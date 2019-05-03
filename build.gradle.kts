import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.internal.os.OperatingSystem

group = "com.pg"
version = "1.0-SNAPSHOT"

val lwjglVersion = "3.2.1"
val jomlVersion = "1.9.10"

val lwjglNatives = when (OperatingSystem.current()) {
    OperatingSystem.LINUX -> "natives-linux"
    OperatingSystem.MAC_OS -> "natives-macos"
    OperatingSystem.WINDOWS -> "natives-windows"
    else -> throw Error("Unrecognized or unsupported Operating system. Please set \"lwjglNatives\" manually")
}

plugins {
    kotlin("jvm") version "1.2.51"
}

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("org.lwjgl", "lwjgl"       , lwjglVersion)
    compile("org.lwjgl", "lwjgl-assimp", lwjglVersion)
    compile("org.lwjgl", "lwjgl-glfw"  , lwjglVersion)
    compile("org.lwjgl", "lwjgl-openal", lwjglVersion)
    compile("org.lwjgl", "lwjgl-opengl", lwjglVersion)
    compile("org.lwjgl", "lwjgl-stb"   , lwjglVersion)
    compile("org.lwjgl", "lwjgl"       , lwjglVersion, classifier = lwjglNatives)
    compile("org.lwjgl", "lwjgl-assimp", lwjglVersion, classifier = lwjglNatives)
    compile("org.lwjgl", "lwjgl-glfw"  , lwjglVersion, classifier = lwjglNatives)
    compile("org.lwjgl", "lwjgl-openal", lwjglVersion, classifier = lwjglNatives)
    compile("org.lwjgl", "lwjgl-opengl", lwjglVersion, classifier = lwjglNatives)
    compile("org.lwjgl", "lwjgl-stb"   , lwjglVersion, classifier = lwjglNatives)
    compile("org.joml", "joml"         , jomlVersion)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}