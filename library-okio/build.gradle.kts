import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.maven.publish)
}

group = "dev.zwander"

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.RequiresOptIn")
}

val javaVersionEnum: JavaVersion = JavaVersion.VERSION_21

kotlin {
    jvmToolchain(javaVersionEnum.toString().toInt())

    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.addAll("-opt-in=kotlin.RequiresOptIn", "-Xdont-warn-on-error-suppression")
                    jvmTarget = JvmTarget.fromTarget(javaVersionEnum.toString())
                }
            }
        }

        publishLibraryVariants("release")
    }

    jvm {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget = JvmTarget.fromTarget(javaVersionEnum.toString())
                }
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosX64(),
        macosArm64(),
        watchosArm32(),
        watchosArm64(),
        watchosDeviceArm64(),
        watchosSimulatorArm64(),
        watchosX64(),
        tvosArm64(),
        tvosSimulatorArm64(),
        tvosX64(),
    ).forEach {
        it.binaries.framework {
            baseName = "KMPFile-Okio"
            isStatic = true
        }
    }

    mingwX64()

    linuxArm64()
    linuxX64()

    targets.all {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.addAll("-Xexpect-actual-classes", "-Xdont-warn-on-error-suppression")
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.io.core)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.okio)
            }
        }

        val posixMain by creating {
            dependsOn(commonMain)
        }

        val androidAndJvmMain by creating {
            dependsOn(commonMain)
        }

        val jvmMain by getting {
            dependsOn(androidAndJvmMain)
        }

        val androidMain by getting {
            dependsOn(androidAndJvmMain)

            dependencies {
                implementation(libs.kotlinx.coroutines.android)
            }
        }

        val appleMain by creating {
            dependsOn(commonMain)
        }

        val iosMain by creating {
            dependsOn(appleMain)
        }

        val iosX64Main by getting {
            dependsOn(iosMain)
        }

        val iosArm64Main by getting {
            dependsOn(iosMain)
        }

        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }

        val macosMain by creating {
            dependsOn(appleMain)
        }

        val macosArm64Main by getting {
            dependsOn(macosMain)
        }

        val macosX64Main by getting {
            dependsOn(macosMain)
        }

        val watchosMain by creating {
            dependsOn(appleMain)
        }

        val watchosArm32Main by getting {
            dependsOn(watchosMain)
        }

        val watchosArm64Main by getting {
            dependsOn(watchosMain)
        }

        val watchosDeviceArm64Main by getting {
            dependsOn(watchosMain)
        }

        val watchosSimulatorArm64Main by getting {
            dependsOn(watchosMain)
        }

        val watchosX64Main by getting {
            dependsOn(watchosMain)
        }

        val tvosMain by creating {
            dependsOn(appleMain)
        }

        val tvosArm64Main by getting {
            dependsOn(tvosMain)
        }

        val tvosSimulatorArm64Main by getting {
            dependsOn(tvosMain)
        }

        val tvosX64Main by getting {
            dependsOn(tvosMain)
        }

        val mingwMain by creating {
            dependsOn(commonMain)
        }

        val mingwX64Main by getting {
            dependsOn(mingwMain)
        }

        val linuxMain by creating {
            dependsOn(posixMain)
        }

        val linuxArm64Main by getting {
            dependsOn(linuxMain)
        }

        val linuxX64Main by getting {
            dependsOn(linuxMain)
        }
    }
}

android {
    this.compileSdk = 34

    defaultConfig {
        this.minSdk = 21
    }

    namespace = "dev.zwander.kotlin.file.okio"

    compileOptions {
        sourceCompatibility = javaVersionEnum
        targetCompatibility = javaVersionEnum
    }

    buildFeatures {
        aidl = true
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

mavenPublishing {
    coordinates(artifactId = "kmpfile-okio")
}
