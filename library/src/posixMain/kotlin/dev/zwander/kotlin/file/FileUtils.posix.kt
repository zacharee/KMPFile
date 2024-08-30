package dev.zwander.kotlin.file

@Suppress("unused")
actual object FileUtils {
    actual fun fromString(
        input: String,
        isDirectory: Boolean,
    ): IPlatformFile? {
        return PlatformFile(input)
    }
}