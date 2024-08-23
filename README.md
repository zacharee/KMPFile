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

Once you retrieve a FileKit `PlatformFile`, you can convert it to a KMPFile `PlatformFile` by getting its path:

```kotlin
val fileKitFile = FileKit.pickFile()

if (fileKitFile != null) {
    val kmpFile = dev.zwander.kotlin.file.PlatformFile(fileKitFile.path)
    val source = kmpFile.inputStream()
    //...
}
```

### Android Uri Handling
In most cases on Android, you'll probably be using Uri instead of File. KMPFile provides the Android-specific `PlatformUriFile` for these cases. The constructor takes a Context object and a DocumentFile representing a Uri.

To convert from FileKit (this needs to be done in the Android source set):

```kotlin
val fileKitFile = FileKit.pickFile()

if (fileKitFile != null) {
    val kmpFile = dev.zwander.kotlin.file.PlatformUriFile(context, DocumentFile.fromSingleUri(fileKitFile.uri)!!)
    val source = kmpFile.inputStream()
    //...
}
```
