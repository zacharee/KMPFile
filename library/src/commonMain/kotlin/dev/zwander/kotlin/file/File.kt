package dev.zwander.kotlin.file

import kotlinx.io.Sink
import kotlinx.io.Source

/**
 * Platforms should actuate this class to implement
 * various filesystem classes they use.
 */
expect open class PlatformFile : IPlatformFile {
    companion object;

    constructor(pathName: String)
    constructor(parent: String, child: String)
    constructor(parent: PlatformFile, child: String)

    override val nameWithoutExtension: String

    override fun getName(): String
    override fun getParent(): String?
    override fun getParentFile(): IPlatformFile?
    override fun getPath(): String
    override fun isAbsolute(): Boolean
    override fun getAbsolutePath(): String
    override fun getAbsoluteFile(): IPlatformFile
    override fun getCanonicalPath(): String
    override fun getCanonicalFile(): IPlatformFile
    override fun getCanRead(): Boolean
    override fun getCanWrite(): Boolean
    override fun getExists(): Boolean
    override fun isDirectory(): Boolean
    override fun isFile(): Boolean
    override fun isHidden(): Boolean
    override fun getLastModified(): Long
    override fun getLength(): Long
    override fun getTotalSpace(): Long
    override fun getFreeSpace(): Long
    override fun getUsableSpace(): Long

    override fun createNewFile(): Boolean
    override fun delete(): Boolean
    override fun deleteOnExit()
    override fun list(): Array<String>?
    override fun list(filter: (dir: IPlatformFile, name: String) -> Boolean): Array<String>?
    override fun listFiles(): Array<IPlatformFile>?
    override fun listFiles(filter: (dir: IPlatformFile, name: String) -> Boolean): Array<IPlatformFile>?
    override fun listFiles(filter: (pathName: IPlatformFile) -> Boolean): Array<IPlatformFile>?
    override fun mkdir(): Boolean
    override fun mkdirs(): Boolean
    override fun renameTo(dest: IPlatformFile): Boolean
    override fun setLastModified(time: Long): Boolean
    override fun setReadOnly(): Boolean
    override fun setWritable(writable: Boolean, ownerOnly: Boolean): Boolean
    override fun setWritable(writable: Boolean): Boolean
    override fun setReadable(readable: Boolean, ownerOnly: Boolean): Boolean
    override fun setReadable(readable: Boolean): Boolean
    override fun setExecutable(executable: Boolean, ownerOnly: Boolean): Boolean
    override fun setExecutable(executable: Boolean): Boolean
    override fun canExecute(): Boolean

    override fun openOutputStream(append: Boolean): Sink?
    override fun openInputStream(): Source?

    override fun child(childName: String, isDirectory: Boolean, mimeType: String): IPlatformFile?

    override fun hashCode(): Int
    override fun equals(other: Any?): Boolean
    override fun compareTo(other: IPlatformFile): Int
}

operator fun PlatformFile.Companion.invoke(pathName: String): PlatformFile {
    return PlatformFile(pathName)
}

operator fun PlatformFile.Companion.invoke(parent: String, child: String): PlatformFile {
    return PlatformFile(parent, child)
}

operator fun PlatformFile.Companion.invoke(parent: PlatformFile, child: String): PlatformFile {
    return PlatformFile(parent, child)
}

/**
 * The base File representation for Platform Files to
 * override if needed.
 */
interface IPlatformFile : Comparable<IPlatformFile> {
    /**
     * Returns the name of the file without any extension.
     * e.g., "filename.txt" => "filename"
     */
    val nameWithoutExtension: String

    /**
     * Returns the file's full name, including extension.
     */
    fun getName(): String

    /**
     * Returns the file's parent path, if supported.
     * On Android, with PlatformUriFile, this may return null.
     */
    fun getParent(): String?

    /**
     * Returns the file's parent as a file, if supported.
     * On Android, with PlatformUriFile, this may return null.
     */
    fun getParentFile(): IPlatformFile?

    /**
     * Get the file path.
     * With Android's PlatformUriFile, this returns the Uri as a String.
     */
    fun getPath(): String

    /**
     * Whether this is an absolute file reference.
     * Always false for PlatformUriFile.
     */
    fun isAbsolute(): Boolean

    /**
     * Returns the absolute path to this file.
     * Equal to getPath() with PlatformUriFile.
     */
    fun getAbsolutePath(): String

    /**
     * Returns this file as an absolute file.
     * With PlatformUriFile, this returns the current file.
     */
    fun getAbsoluteFile(): IPlatformFile

    /**
     * Returns the canonical file path, if supported.
     * On Apple platforms, this is equivalent to the absolute path.
     * Not supported with PlatformUriFile.
     * @throws IllegalStateException otherwise.
     */
    fun getCanonicalPath(): String

    /**
     * Returns the canonical file, if supported.
     * On Apple platforms, this is equivalent to the absolute file.
     * Not supported with PlatformUriFile.
     * @throws IllegalStateException otherwise.
     */
    fun getCanonicalFile(): IPlatformFile

    /**
     * Returns whether the app can read this file.
     */
    fun getCanRead(): Boolean

    /**
     * Returns whether the app can write to this file.
     */
    fun getCanWrite(): Boolean

    /**
     * Returns whether this file exists.
     * With PlatformUriFile, this always returns true.
     */
    fun getExists(): Boolean

    /**
     * Returns whether this file instance refers to a directory.
     */
    fun isDirectory(): Boolean

    /**
     * Returns whether this file instance refers to a file.
     */
    fun isFile(): Boolean

    /**
     * Returns whether this file is hidden.
     * Unsupported with PlatformUriFile.
     */
    fun isHidden(): Boolean

    /**
     * Returns when this file was last modified, in milliseconds since the Unix epoch.
     */
    fun getLastModified(): Long

    /**
     * Get the size of the file in bytes.
     */
    fun getLength(): Long

    /**
     * Get the total space available on the file's filesystem.
     * Unsupported with PlatformUriFile.
     */
    fun getTotalSpace(): Long

    /**
     * Get the total free space on the file's filesystem.
     * Unsupported with PlatformUriFile.
     */
    fun getFreeSpace(): Long

    /**
     * Get the total usable space on the file's filesystem.
     * Unsupported with PlatformUriFile.
     */
    fun getUsableSpace(): Long

    /**
     * Create a new file or at the path specified by this instance.
     * With PlatformUriFile, the file is automatically created.
     */
    fun createNewFile(): Boolean

    /**
     * Delete the file or directory at the path specified by this instance.
     */
    fun delete(): Boolean

    /**
     * Delete the file or directory at the path specified by this instance
     * when the app quits.
     * Not supported on Apple or with PlatformUriFile.
     */
    fun deleteOnExit()

    /**
     * List child files if this is a directory.
     */
    fun list(): Array<String>?

    /**
     * List child files if this is a directory.
     */
    fun list(filter: (dir: IPlatformFile, name: String) -> Boolean): Array<String>?

    /**
     * List child files if this is a directory.
     */
    fun listFiles(): Array<IPlatformFile>?

    /**
     * List child files if this is a directory.
     */
    fun listFiles(filter: (dir: IPlatformFile, name: String) -> Boolean): Array<IPlatformFile>?

    /**
     * List child files if this is a directory.
     */
    fun listFiles(filter: (pathName: IPlatformFile) -> Boolean): Array<IPlatformFile>?

    /**
     * Creates a directory at the path referenced by this instance.
     * With PlatformUriFile, directories are automatically created.
     */
    fun mkdir(): Boolean

    /**
     * Creates a directory at the path referenced by this instance, along
     * with intermediate directories as needed.
     * With PlatformUriFile, directories are automatically created.
     */
    fun mkdirs(): Boolean

    /**
     * Moves the file at the path specified by this instance to the path specified by the passed instance.
     */
    fun renameTo(dest: IPlatformFile): Boolean

    /**
     * Set the last modified time in milliseconds since the Unix epoch.
     * Unsupported with PlatformUriFile.
     */
    fun setLastModified(time: Long): Boolean

    /**
     * Mark the file as read only.
     * Unsupported with PlatformUriFile.
     */
    fun setReadOnly(): Boolean

    /**
     * Update the write permissions on this file.
     * Unsupported with PlatformUriFile.
     */
    fun setWritable(writable: Boolean, ownerOnly: Boolean): Boolean

    /**
     * Update the write permissions on this file for the owner.
     * Unsupported with PlatformUriFile.
     */
    fun setWritable(writable: Boolean): Boolean

    /**
     * Update the read permissions on this file.
     * Unsupported with PlatformUriFile.
     */
    fun setReadable(readable: Boolean, ownerOnly: Boolean): Boolean

    /**
     * Update the read permissions on this file for the owner.
     * Unsupported with PlatformUriFile.
     */
    fun setReadable(readable: Boolean): Boolean

    /**
     * Set whether this file is executable.
     * Unsupported with PlatformUriFile.
     */
    fun setExecutable(executable: Boolean, ownerOnly: Boolean): Boolean

    /**
     * Set whether this file is executable for the owner.
     * Unsupported with PlatformUriFile.
     */
    fun setExecutable(executable: Boolean): Boolean

    /**
     * Get whether the current user can execute this file.
     * Unsupported with PlatformUriFile.
     */
    fun canExecute(): Boolean

    /**
     * Obtain a write stream to this file.
     */
    fun openOutputStream(append: Boolean = false): Sink?

    /**
     * Obtain a read stream from this file.
     */
    fun openInputStream(): Source?

    /**
     * Retrieve a reference to a child file of this file.
     */
    fun child(childName: String, isDirectory: Boolean, mimeType: String = "*/*"): IPlatformFile?

    override fun hashCode(): Int
    override fun equals(other: Any?): Boolean
    override fun compareTo(other: IPlatformFile): Int
}
