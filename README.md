# KMPFile
A Kotlin Multiplatform File implementation that mimics the Java File API.

KMPFile wraps [kotlinx.io](https://github.com/Kotlin/kotlinx-io) for some operations including streams.

## Compatibility
KMPFile targets the following platforms:
- Android (File, Uri)
- JVM
- Apple (iOS, macOS, tvOS, watchOS) (largely untested)
- Linux (arm64, x64) (largely untested)
- Android NDK (arm32, arm64, x86, x64) (largely untested)
- Windows (MinGW x64) (largely untested)

## Installation
![Maven Central Version](https://img.shields.io/maven-central/v/dev.zwander/kmpfile)

Add the dependency to your `commonMain` source set:

```kotlin
sourceSets {
    val commonMain by getting {
        dependencies {
            implementation("dev.zwander:kmpfile:VERSION")
            // Convenience functions for converting from FileKit to KMPFile.
            // Supports the same platforms as FileKit, minus JS and WASM.
            implementation("dev.zwander:kmpfile-filekit:VERSION")
        }
    }
}
```

## Usage
The base type you want to use in your code (e.g., when specifying function arguments) is `IPlatformFile`. This interface contains all the supported functions of KMPFile.

```kotlin
suspend fun someFileOperation(input: IPlatformFile, output: IPlatformFile) {
    //...
}
```

There are two classes that implement `IPlatformFile`: `PlatformFile` and `PlatformUriFile`.

### PlatformFile
This class is available on all platforms and is meant to deal with files directly.

On Android and the JVM, `PlatformFile` wraps Java's `File`. On Apple platforms, it wraps `NSURL`.

`PlatformFile` contains multiple constructors to create instances directly from common code. You can create a `PlatformFile` from a file path, from a parent path and child file name, from a parent file and child file name, and even wrap a Java File or Apple NSURL directly from your common source.

### PlatformUriFile
This class is only available on Android, but implements `IPlatformFile` so it can be used in the same way as `PlatformFile`.

`PlatformUriFile` is made for doing Uri-based file management on Android. You can't directly create an instance from common code, but there are convenience functions for doing so.

### Convenience Functions
If you have a string that could represent either a file path or Uri, you can use `FileUtils.fromString()` to create the appropriate `IPlatformFile` instance for it.

```kotlin
// This will create a PlatformUriFile instance on Android.
val uriFile = FileUtils.fromString(input = "content://media/some_file_id", isDirectory = false)

// This will create a PlatformFile instance on all platforms.
val realFile = FileUtils.fromString(input = "/path/to/some_file.txt", isDirectory = false)
```

Specifying whether the referenced path is a directory is necessary for consistent behavior with Android Uris and on Apple platforms.

### Platform Differences
File management on one platform isn't perfectly analogous to management on another, so not every function of `IPlatformFile` may be available or identical in behavior.

For details on the differences between platforms and which functions aren't supported on which platform, check out the comments on `IPlatformFile`.

### Picking Files
KMPFile doesn't provide file picker functionality, but the [FileKit](https://github.com/vinceglb/FileKit/) can be used with KMPFile.

KMPFile provides an extension module `dev.zwander:kmpfile-filekit` that you can implement to provide extension functions to convert FileKit files to KMPFile files.

```kotlin
val fileKitDirectory = FileKit.pickDirectory()
val fileKitFile = FileKit.pickFile()

val kmpFileDirectory = fileKitDirectory.toKmpFile()
val kmpFileFile = fileKitFile.toKmpFile()
```

On Android, `toKmpFile()` will return a `PlatformUriFile` instance, since FileKit's file on Android only handles Uris.

`PlatformUriFile` implements `IPlatformFile` so this is largely invisible to usages in the common source set. You can create your own instances of `PlatformUriFile` from the Android source set.
