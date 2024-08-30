package dev.zwander.kotlin.file

import kotlinx.io.Sink
import kotlinx.io.Source

/**
 * The base File representation for Platform Files to
 * override if needed.
 */
interface IPlatformFile : Comparable<IPlatformFile> {
    /**
     * Returns the name of the file without any extension.
     * e.g., if the file's full name is "filename.txt", this will return "filename".
     * e.g., if the file's full name is "filename.txt.old", this will return "filename.txt".
     * @Android PlatformUriFile will return the entire Uri as a string if the name isn't available.
     */
    val nameWithoutExtension: String

    /**
     * @return the file's full name, including extension, e.g., "filename.txt".
     */
    fun getName(): String

    /**
     * @Android this may return null even if the file instance isn't at the root of the filesystem,
     * since [dev.zwander.kotlin.file.PlatformUriFile] can't traverse above the file or directory chosen by the user.
     * @return the file's parent path, if available.
     */
    fun getParent(): String?

    /**
     * @Android this may return null even if the file instance isn't at the root of the filesystem,
     * since [dev.zwander.kotlin.file.PlatformUriFile] can't traverse above the file or directory chosen by the user.
     * @return the file's parent as a file, if supported.
     */
    fun getParentFile(): IPlatformFile?

    /**
     * Get the file path.
     * @Android returns the Uri as a String.
     * @return the path of the file or a string representation of a Uri.
     */
    fun getPath(): String

    /**
     * @Android always false if this is a [dev.zwander.kotlin.file.PlatformUriFile].
     * @return whether this is an absolute file reference.
     */
    fun isAbsolute(): Boolean

    /**
     * @Android equal to [getPath] if this is a [dev.zwander.kotlin.file.PlatformUriFile].
     * @return the absolute path to this file.
     */
    fun getAbsolutePath(): String

    /**
     * @Android returns the current file instance if this is a [dev.zwander.kotlin.file.PlatformUriFile].
     * @return this file as an absolute file.
     */
    fun getAbsoluteFile(): IPlatformFile

    /**
     * @Apple equal to [getAbsolutePath].
     * @Android unsupported with [dev.zwander.kotlin.file.PlatformUriFile].
     * @return the canonical file path, if supported.
     * @throws IllegalStateException otherwise.
     */
    fun getCanonicalPath(): String

    /**
     * @Apple equal to [getAbsoluteFile].
     * @Android unsupported with [dev.zwander.kotlin.file.PlatformUriFile].
     * @return the canonical file, if supported.
     * @throws IllegalStateException otherwise.
     */
    fun getCanonicalFile(): IPlatformFile

    /**
     * @return whether the app can read this file.
     */
    fun getCanRead(): Boolean

    /**
     * @return whether the app can write to this file.
     */
    fun getCanWrite(): Boolean

    /**
     * @Android always true with [dev.zwander.kotlin.file.PlatformUriFile].
     * @return whether this file exists.
     */
    fun getExists(): Boolean

    /**
     * @return whether this file instance refers to a directory.
     */
    fun isDirectory(): Boolean

    /**
     * @return whether this file instance refers to a file.
     */
    fun isFile(): Boolean

    /**
     * @Android unsupported with [dev.zwander.kotlin.file.PlatformUriFile].
     * @POSIX currently only checks to see if the file name begins with ".".
     * @MinGW returns true also if the file is a system file.
     * @return whether this file is hidden.
     */
    fun isHidden(): Boolean

    /**
     * @Apple returns a millisecond value, but only with second-level precision.
     * @return when this file was last modified, in milliseconds since the Unix epoch.
     */
    fun getLastModified(): Long

    /**
     * @return the size of the file in bytes.
     */
    fun getLength(): Long

    /**
     * @Android unsupported with [dev.zwander.kotlin.file.PlatformUriFile].
     * @POSIX unsupported.
     * @MinGW unsupported.
     * @return the total space available on the file's filesystem.
     */
    fun getTotalSpace(): Long

    /**
     * @Android unsupported with [dev.zwander.kotlin.file.PlatformUriFile].
     * @POSIX unsupported.
     * @MinGW unsupported.
     * @return the total free space on the file's filesystem.
     */
    fun getFreeSpace(): Long

    /**
     * @Android unsupported with [dev.zwander.kotlin.file.PlatformUriFile].
     * @Apple equal to [getFreeSpace].
     * @POSIX unsupported.
     * @MinGW unsupported.
     * @return the total usable space on the file's filesystem.
     */
    fun getUsableSpace(): Long

    /**
     * Create a new file or at the path specified by this instance.
     * @Android files are automatically created with [dev.zwander.kotlin.file.PlatformUriFile].
     * @return whether the file was successfully created.
     */
    fun createNewFile(): Boolean

    /**
     * Delete the file or directory at the path specified by this instance.
     * @return whether the file was successfully deleted.
     */
    fun delete(): Boolean

    /**
     * @Apple unsupported.
     * @POSIX unsupported.
     * @MinGW unsupported.
     * @Android unsupported with [dev.zwander.kotlin.file.PlatformUriFile].
     * Delete the file or directory at the path specified by this instance
     * when the app quits.
     */
    fun deleteOnExit()

    /**
     * List child files if this is a directory.
     * @return a list of child file paths.
     */
    fun list(): Array<String>?

    /**
     * List child files if this is a directory.
     * @param filter a method to filter the returned child files.
     * @return a filtered list of child file paths.
     */
    fun list(filter: (dir: IPlatformFile, name: String) -> Boolean): Array<String>?

    /**
     * List child files if this is a directory.
     * @return a list of child files.
     */
    fun listFiles(): Array<IPlatformFile>?

    /**
     * List child files if this is a directory.
     * @param filter a method to filter the returned child files.
     * @return a filtered list of child files.
     */
    fun listFiles(filter: (dir: IPlatformFile, name: String) -> Boolean): Array<IPlatformFile>?

    /**
     * List child files if this is a directory.
     * @param filter a method to filter the returned child files.
     * @return a filtered list of child files.
     */
    fun listFiles(filter: (pathName: IPlatformFile) -> Boolean): Array<IPlatformFile>?

    /**
     * @Android directories are automatically created with [dev.zwander.kotlin.file.PlatformUriFile].
     * Creates a directory at the path referenced by this instance.
     * @return whether creating the directory was successful.
     */
    fun mkdir(): Boolean

    /**
     * @Android directories are automatically created with [dev.zwander.kotlin.file.PlatformUriFile].
     * Creates a directory at the path referenced by this instance, along
     * with intermediate directories as needed.
     * @return whether creating the directories was successful.
     */
    fun mkdirs(): Boolean

    /**
     * Moves the file at the path specified by this instance to the path specified by the passed instance.
     * @param dest a reference to the location this instance should be moved to.
     * @return whether the move was successful.
     */
    fun renameTo(dest: IPlatformFile): Boolean

    /**
     * @Android unsupported with [dev.zwander.kotlin.file.PlatformUriFile].
     * Set the last modified time in milliseconds since the Unix epoch.
     * @param time time in milliseconds since the Unix epoch.
     * @return whether the update was successful.
     */
    fun setLastModified(time: Long): Boolean

    /**
     * @Android unsupported with [dev.zwander.kotlin.file.PlatformUriFile].
     * Mark the file as read only.
     * @return whether the update was successful.
     */
    fun setReadOnly(): Boolean

    /**
     * @Android unsupported with [dev.zwander.kotlin.file.PlatformUriFile].
     * Update the write permissions on this file.
     * @param writable whether this file should be marked as writable.
     * @param ownerOnly whether this update should apply to only the owner or to all users.
     * @return whether the update was successful.
     */
    fun setWritable(writable: Boolean, ownerOnly: Boolean): Boolean

    /**
     * @Android unsupported with [dev.zwander.kotlin.file.PlatformUriFile].
     * Update the write permissions on this file for the owner.
     * @param writable whether this file should be writable.
     * @return whether the update was successful.
     */
    fun setWritable(writable: Boolean): Boolean

    /**
     * @Android unsupported with [dev.zwander.kotlin.file.PlatformUriFile].
     * Update the write permissions on this file.
     * @param readable whether this file should be marked as readable.
     * @param ownerOnly whether this update should apply to only the owner or to all users.
     * @return whether the update was successful.
     */
    fun setReadable(readable: Boolean, ownerOnly: Boolean): Boolean

    /**
     * @Android unsupported with [dev.zwander.kotlin.file.PlatformUriFile].
     * Update the write permissions on this file.
     * @param readable whether this file should be marked as readable.
     * @return whether the update was successful.
     */
    fun setReadable(readable: Boolean): Boolean

    /**
     * @Android unsupported with [dev.zwander.kotlin.file.PlatformUriFile].
     * Update the write permissions on this file.
     * @param executable whether this file should be marked as executable.
     * @param ownerOnly whether this update should apply to only the owner or to all users.
     * @return whether the update was successful.
     */
    fun setExecutable(executable: Boolean, ownerOnly: Boolean): Boolean

    /**
     * @Android unsupported with [dev.zwander.kotlin.file.PlatformUriFile].
     * Update the write permissions on this file.
     * @param executable whether this file should be marked as readable.
     * @return whether the update was successful.
     */
    fun setExecutable(executable: Boolean): Boolean

    /**
     * @Android unsupported with [dev.zwander.kotlin.file.PlatformUriFile].
     * @return whether the current user can execute this file.
     */
    fun canExecute(): Boolean

    /**
     * Obtain a write stream to this file.
     * @param append if true, writes to the stream will append to this file. Otherwise, any existing content will be overwritten.
     * @return a [Sink] instance or null if a stream could not be opened.
     */
    fun openOutputStream(append: Boolean = false): Sink?

    /**
     * Obtain a read stream from this file.
     * @return a [Source] instance or null if a stream could not be opened.
     */
    fun openInputStream(): Source?

    /**
     * Retrieve a reference to a child of this file. Since working with file trees can be different depending on the platform,
     * it's recommended you use this function instead of creating your own [PlatformFile] instance with paths.
     *
     * For example, on Android, you can't be sure that a given [IPlatformFile] instance isn't a [dev.zwander.kotlin.file.PlatformUriFile] instance.
     * Constructing a new [PlatformFile] by calling [getAbsolutePath] on [dev.zwander.kotlin.file.PlatformUriFile] will lead to a file instance
     * that thinks it's handling a normal Java File with its path set to the string representation of an Android Uri.
     *
     * @param childName the name of the child file.
     * @param isDirectory whether the child is a directory.
     * @param mimeType (only used with [dev.zwander.kotlin.file.PlatformUriFile]) the MIME type of the file, if it needs to be created.
     * @return a reference to the child file.
     */
    fun child(childName: String, isDirectory: Boolean, mimeType: String = "*/*"): IPlatformFile?

    override fun hashCode(): Int
    override fun equals(other: Any?): Boolean
    override fun compareTo(other: IPlatformFile): Int
}
