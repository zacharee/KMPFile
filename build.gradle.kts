plugins {
    id("com.android.library") version "8.5.2" apply false
    id("org.jetbrains.kotlin.multiplatform") version "2.0.10" apply false
    id("com.vanniktech.maven.publish") version "0.29.0" apply false
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.addAll("-Xskip-prerelease-check", "-Xdont-warn-on-error-suppression")
    }
}
