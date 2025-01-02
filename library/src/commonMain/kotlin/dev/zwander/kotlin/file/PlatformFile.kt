package dev.zwander.kotlin.file

import kotlinx.io.Sink
import kotlinx.io.Source

/**
 * This is the class platforms instantiate for dealing directly with
 * files on a filesystem. For detailed documentation on the functions,
 * see [IPlatformFile].
 */
@Suppress("KDocUnresolvedReference")
expect open class PlatformFile : IPlatformFile {
    companion object;

    /**
     * Create a [PlatformFile] from a given path.
     * When creating a directory reference, ensure the path has a trailing "/".
     */
    constructor(pathName: String)

    /**
     * Create a [PlatformFile] from a given parent path and child name.
     * When creating a directory reference, ensure the child name has a trailing "/".
     */
    constructor(parent: String, child: String)

    /**
     * Create a [PlatformFile] from a given parent file and child name.
     * When creating a directory reference, ensure the child name has a trailing "/".
     */
    constructor(parent: PlatformFile, child: String)

    /**
     * Create a [PlatformFile] from a given platform-specific file instance.
     * On Android and Java, [RealFile] is an alias for [java.io.File].
     * On Apple, [RealFile] is an alias for [platform.Foundation.NSURL].
     */
    constructor(realFile: RealFile)

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

    override fun openOutputStream(append: Boolean, truncate: Boolean): Sink?
    override fun openInputStream(): Source?

    override fun child(childName: String, isDirectory: Boolean, mimeType: String): IPlatformFile?

    override fun hashCode(): Int
    override fun equals(other: Any?): Boolean
    override fun compareTo(other: IPlatformFile): Int
    override fun toString(): String
}
