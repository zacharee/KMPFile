package dev.zwander.kotlin.file

@Suppress("unused")
expect object FileUtils {
    /**
     * Attempts to create a file reference from the given input.
     * @Apple will attempt to construct an NSURL using the input and return [PlatformFile].
     * @JVM will attempt to construct a File using the input and return [PlatformFile].
     * @Android will attempt to construct first a [dev.zwander.kotlin.file.PlatformUriFile] from the input,
     * creating a [PlatformFile] instead if the input is not a valid Android Uri.
     *
     * @param input the input as a file path or Uri representation.
     * @param isDirectory whether the file instance should reference a directory.
     * @return a file reference, or null if one couldn't be created.
     */
    fun fromString(input: String, isDirectory: Boolean): IPlatformFile?
}