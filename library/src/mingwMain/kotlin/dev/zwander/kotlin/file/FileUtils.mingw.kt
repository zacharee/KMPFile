package dev.zwander.kotlin.file

@Suppress("unused", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object FileUtils {
    actual fun fromString(
        input: String,
        isDirectory: Boolean,
    ): IPlatformFile? {
        return PlatformFile(input)
    }
}