# KMPFile
A Kotlin Multiplatform File implementation that mimics the Java File API.

KMPFile wraps [kotlinx.io](https://github.com/Kotlin/kotlinx-io) for some operations including streams.

## Compatibility
KMPFile targets the following platforms:
- Android (File, Uri)
- JVM
- iOS (largely untested)
- macOS native (largely untested)

## Installation
![Maven Central Version](https://img.shields.io/maven-central/v/dev.zwander/kmpfile)

Add the dependency to your `commonMain` source set:

```kotlin
sourceSets {
    val commonMain by getting {
        dependencies {
            implementation("dev.zwander:kmpfile:VERSION")
            // Convenience functions for converting from FileKit to KMPFile.
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

To create instances of `IPlatformFile`, use the constructors for `PlatformFile` on the respective platform.
- Android and JVM take a `java.io.File` instance in the constructor.
- iOS and macOS take an `NSURL` instance in the constructor.

If you know the absolute path of the file you want to reference, you can also construct a `PlatformFile` instance directly in your common code:
```kotlin
// Create a file from a path string.
val file = PlatformFile(absolutePathString)

// Create a directory and child file.
val directory = PlatformFile(pathToDirectory)
val childFile = PlatformFile(directory, "someChild.txt")
```

Note that on iOS and macOS, the system differentiates directories and files by the presence of a trailing slash in the path. KMPFile doesn't currently handle this automatically, so when creating a `PlatformFile` in common code that represents a directory, make sure you have that trailing slash.

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
