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
    override suspend fun getParent(): String?
    override suspend fun getParentFile(): IPlatformFile?
    override fun getPath(): String
    override suspend fun isAbsolute(): Boolean
    override fun getAbsolutePath(): String
    override fun getAbsoluteFile(): IPlatformFile
    override suspend fun getCanonicalPath(): String
    override suspend fun getCanonicalFile(): IPlatformFile
    override suspend fun getCanRead(): Boolean
    override suspend fun getCanWrite(): Boolean
    override suspend fun getExists(): Boolean
    override suspend fun isDirectory(): Boolean
    override suspend fun isFile(): Boolean
    override suspend fun isHidden(): Boolean
    override suspend fun getLastModified(): Long
    override suspend fun getLength(): Long
    override suspend fun getTotalSpace(): Long
    override suspend fun getFreeSpace(): Long
    override suspend fun getUsableSpace(): Long

    override suspend fun createNewFile(): Boolean
    override suspend fun delete(): Boolean
    override suspend fun deleteOnExit()
    override suspend fun list(): Array<String>?
    override suspend fun list(filter: (dir: IPlatformFile, name: String) -> Boolean): Array<String>?
    override suspend fun listFiles(): Array<IPlatformFile>?
    override suspend fun listFiles(filter: (dir: IPlatformFile, name: String) -> Boolean): Array<IPlatformFile>?
    override suspend fun listFiles(filter: (pathName: IPlatformFile) -> Boolean): Array<IPlatformFile>?
    override suspend fun mkdir(): Boolean
    override suspend fun mkdirs(): Boolean
    override suspend fun renameTo(dest: IPlatformFile): Boolean
    override suspend fun setLastModified(time: Long): Boolean
    override suspend fun setReadOnly(): Boolean
    override suspend fun setWritable(writable: Boolean, ownerOnly: Boolean): Boolean
    override suspend fun setWritable(writable: Boolean): Boolean
    override suspend fun setReadable(readable: Boolean, ownerOnly: Boolean): Boolean
    override suspend fun setReadable(readable: Boolean): Boolean
    override suspend fun setExecutable(executable: Boolean, ownerOnly: Boolean): Boolean
    override suspend fun setExecutable(executable: Boolean): Boolean
    override suspend fun canExecute(): Boolean

    override suspend fun openOutputStream(append: Boolean): Sink?
    override suspend fun openInputStream(): Source?

    override suspend fun child(childName: String): IPlatformFile?

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
    suspend fun getParent(): String?
    suspend fun getParentFile(): IPlatformFile?
    fun getPath(): String
    suspend fun isAbsolute(): Boolean
    fun getAbsolutePath(): String
    fun getAbsoluteFile(): IPlatformFile
    suspend fun getCanonicalPath(): String
    suspend fun getCanonicalFile(): IPlatformFile
    suspend fun getCanRead(): Boolean
    suspend fun getCanWrite(): Boolean
    suspend fun getExists(): Boolean
    suspend fun isDirectory(): Boolean
    suspend fun isFile(): Boolean
    suspend fun isHidden(): Boolean
    suspend fun getLastModified(): Long
    suspend fun getLength(): Long
    suspend fun getTotalSpace(): Long
    suspend fun getFreeSpace(): Long
    suspend fun getUsableSpace(): Long

    suspend fun createNewFile(): Boolean
    suspend fun delete(): Boolean
    suspend fun deleteOnExit()
    suspend fun list(): Array<String>?
    suspend fun list(filter: (dir: IPlatformFile, name: String) -> Boolean): Array<String>?
    suspend fun listFiles(): Array<IPlatformFile>?
    suspend fun listFiles(filter: (dir: IPlatformFile, name: String) -> Boolean): Array<IPlatformFile>?
    suspend fun listFiles(filter: (pathName: IPlatformFile) -> Boolean): Array<IPlatformFile>?
    suspend fun mkdir(): Boolean
    suspend fun mkdirs(): Boolean
    suspend fun renameTo(dest: IPlatformFile): Boolean
    suspend fun setLastModified(time: Long): Boolean
    suspend fun setReadOnly(): Boolean
    suspend fun setWritable(writable: Boolean, ownerOnly: Boolean): Boolean
    suspend fun setWritable(writable: Boolean): Boolean
    suspend fun setReadable(readable: Boolean, ownerOnly: Boolean): Boolean
    suspend fun setReadable(readable: Boolean): Boolean
    suspend fun setExecutable(executable: Boolean, ownerOnly: Boolean): Boolean
    suspend fun setExecutable(executable: Boolean): Boolean
    suspend fun canExecute(): Boolean

    suspend fun openOutputStream(append: Boolean = false): Sink?
    suspend fun openInputStream(): Source?

    suspend fun child(childName: String): IPlatformFile?

    override fun hashCode(): Int
    override fun equals(other: Any?): Boolean
    override fun compareTo(other: IPlatformFile): Int
}
