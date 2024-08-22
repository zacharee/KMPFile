package dev.zwander.kotlin.file.attribute

/**
 * An object that encapsulates the value of a file attribute that can be set
 * atomically when creating a new file or directory by invoking the [ ][java.nio.file.Files.createFile] or [ ][java.nio.file.Files.createDirectory] methods.
 *
 * @param <T> The type of the file attribute value
 *
 * @since 1.7
 * @see PosixFilePermissions.asFileAttribute
</T> */
interface FileAttribute<T> {
    /**
     * Returns the attribute name.
     */
    fun name(): String?

    /**
     * Returns the attribute value.
     */
    fun value(): T
}
