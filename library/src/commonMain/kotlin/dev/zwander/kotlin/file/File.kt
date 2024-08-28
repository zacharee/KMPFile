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

    override fun child(childName: String, mimeType: String): IPlatformFile?

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
    val nameWithoutExtension: String

    fun getName(): String
    fun getParent(): String?
    fun getParentFile(): IPlatformFile?
    fun getPath(): String
    fun isAbsolute(): Boolean
    fun getAbsolutePath(): String
    fun getAbsoluteFile(): IPlatformFile
    fun getCanonicalPath(): String
    fun getCanonicalFile(): IPlatformFile
    fun getCanRead(): Boolean
    fun getCanWrite(): Boolean
    fun getExists(): Boolean
    fun isDirectory(): Boolean
    fun isFile(): Boolean
    fun isHidden(): Boolean
    fun getLastModified(): Long
    fun getLength(): Long
    fun getTotalSpace(): Long
    fun getFreeSpace(): Long
    fun getUsableSpace(): Long

    fun createNewFile(): Boolean
    fun delete(): Boolean
    fun deleteOnExit()
    fun list(): Array<String>?
    fun list(filter: (dir: IPlatformFile, name: String) -> Boolean): Array<String>?
    fun listFiles(): Array<IPlatformFile>?
    fun listFiles(filter: (dir: IPlatformFile, name: String) -> Boolean): Array<IPlatformFile>?
    fun listFiles(filter: (pathName: IPlatformFile) -> Boolean): Array<IPlatformFile>?
    fun mkdir(): Boolean
    fun mkdirs(): Boolean
    fun renameTo(dest: IPlatformFile): Boolean
    fun setLastModified(time: Long): Boolean
    fun setReadOnly(): Boolean
    fun setWritable(writable: Boolean, ownerOnly: Boolean): Boolean
    fun setWritable(writable: Boolean): Boolean
    fun setReadable(readable: Boolean, ownerOnly: Boolean): Boolean
    fun setReadable(readable: Boolean): Boolean
    fun setExecutable(executable: Boolean, ownerOnly: Boolean): Boolean
    fun setExecutable(executable: Boolean): Boolean
    fun canExecute(): Boolean

    fun openOutputStream(append: Boolean = false): Sink?
    fun openInputStream(): Source?

    fun child(childName: String, mimeType: String = "*/*"): IPlatformFile?

    override fun hashCode(): Int
    override fun equals(other: Any?): Boolean
    override fun compareTo(other: IPlatformFile): Int
}
