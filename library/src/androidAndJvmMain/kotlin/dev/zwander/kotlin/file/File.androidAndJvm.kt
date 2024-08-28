package dev.zwander.kotlin.file

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * A File implementation that wraps Java's File class.
 */
actual open class PlatformFile : IPlatformFile {
    actual companion object;

    private val wrappedFile: java.io.File

    actual constructor(pathName: String) {
        wrappedFile = java.io.File(pathName)
    }

    actual constructor(parent: String, child: String) {
        wrappedFile = java.io.File(parent, child)
    }

    actual constructor(parent: PlatformFile, child: String) {
        wrappedFile = java.io.File(java.io.File(parent.getAbsolutePath()), child)
    }

    @Suppress("unused")
    constructor(parent: java.io.File, child: String) {
        wrappedFile = java.io.File(parent, child)
    }

    @Suppress("unused")
    constructor(file: java.io.File) {
        wrappedFile = file
    }

    actual override val nameWithoutExtension: String
        get() = wrappedFile.nameWithoutExtension

    actual override fun getName(): String = wrappedFile.name
    actual override fun getParent(): String? = wrappedFile.parent
    actual override fun getParentFile(): IPlatformFile? =
        wrappedFile.parentFile?.absolutePath?.let { PlatformFile(it) }

    actual override fun getPath(): String = wrappedFile.path
    actual override fun isAbsolute(): Boolean = wrappedFile.isAbsolute
    actual override fun getAbsolutePath(): String = wrappedFile.absolutePath
    actual override fun getAbsoluteFile(): IPlatformFile =
        PlatformFile(wrappedFile.absoluteFile.absolutePath)

    actual override fun getCanonicalPath(): String = wrappedFile.canonicalPath
    actual override fun getCanonicalFile(): IPlatformFile =
        PlatformFile(wrappedFile.canonicalFile.absolutePath)

    actual override fun getCanRead(): Boolean = wrappedFile.canRead()
    actual override fun getCanWrite(): Boolean = wrappedFile.canWrite()
    actual override fun getExists(): Boolean = wrappedFile.exists()
    actual override fun isDirectory(): Boolean = wrappedFile.isDirectory
    actual override fun isFile(): Boolean = wrappedFile.isFile
    actual override fun isHidden(): Boolean = wrappedFile.isHidden
    actual override fun getLastModified(): Long = wrappedFile.lastModified()
    actual override fun getLength(): Long = wrappedFile.length()
    actual override fun getTotalSpace(): Long = wrappedFile.totalSpace
    actual override fun getFreeSpace(): Long = wrappedFile.freeSpace
    actual override fun getUsableSpace(): Long = wrappedFile.usableSpace

    actual override fun createNewFile(): Boolean {
        return wrappedFile.createNewFile()
    }

    actual override fun delete(): Boolean {
        return wrappedFile.delete()
    }

    actual override fun deleteOnExit() {
        wrappedFile.deleteOnExit()
    }

    actual override fun list(): Array<String>? {
        return wrappedFile.list()
    }

    actual override fun list(filter: (dir: IPlatformFile, name: String) -> Boolean): Array<String>? {
        return wrappedFile.list { dir, name -> filter(PlatformFile(dir.absolutePath), name) }
    }

    actual override fun listFiles(): Array<IPlatformFile>? {
        return wrappedFile.listFiles()?.map { PlatformFile(it.absolutePath) }
            ?.toTypedArray()
    }

    actual override fun listFiles(filter: (dir: IPlatformFile, name: String) -> Boolean): Array<IPlatformFile>? {
        return wrappedFile.listFiles { dir, name -> filter(PlatformFile(dir.absolutePath), name) }
            ?.map { PlatformFile(it.absolutePath) }
            ?.toTypedArray()
    }

    actual override fun listFiles(filter: (pathName: IPlatformFile) -> Boolean): Array<IPlatformFile>? {
        return wrappedFile.listFiles { pathname -> filter(PlatformFile(pathname.absolutePath)) }
            ?.map { PlatformFile(it.absolutePath) }
            ?.toTypedArray()
    }

    actual override fun mkdir(): Boolean {
        return wrappedFile.mkdir()
    }

    actual override fun mkdirs(): Boolean {
        return wrappedFile.mkdirs()
    }

    actual override fun renameTo(dest: IPlatformFile): Boolean {
        return wrappedFile.renameTo(java.io.File(dest.getAbsolutePath()))
    }

    actual override fun setLastModified(time: Long): Boolean {
        return wrappedFile.setLastModified(time)
    }

    actual override fun setReadOnly(): Boolean {
        return wrappedFile.setReadOnly()
    }

    actual override fun setWritable(writable: Boolean, ownerOnly: Boolean): Boolean {
        return wrappedFile.setWritable(writable, ownerOnly)
    }

    actual override fun setWritable(writable: Boolean): Boolean {
        return wrappedFile.setWritable(writable)
    }

    actual override fun setReadable(readable: Boolean, ownerOnly: Boolean): Boolean {
        return wrappedFile.setReadable(readable, ownerOnly)
    }

    actual override fun setReadable(readable: Boolean): Boolean {
        return wrappedFile.setReadable(readable)
    }

    actual override fun setExecutable(executable: Boolean, ownerOnly: Boolean): Boolean {
        return wrappedFile.setExecutable(executable, ownerOnly)
    }

    actual override fun setExecutable(executable: Boolean): Boolean {
        return wrappedFile.setExecutable(executable)
    }

    actual override fun canExecute(): Boolean {
        return wrappedFile.canExecute()
    }

    actual override fun openOutputStream(append: Boolean): Sink? {
        return FileOutputStream(wrappedFile, append).asSink().buffered()
    }

    actual override fun openInputStream(): Source? {
        return FileInputStream(wrappedFile).asSource().buffered()
    }

    actual override fun child(childName: String, mimeType: String): IPlatformFile? {
        return if (isDirectory()) {
            PlatformFile(wrappedFile, childName)
        } else {
            null
        }
    }

    actual override fun hashCode(): Int {
        return wrappedFile.hashCode()
    }

    actual override fun compareTo(other: IPlatformFile): Int {
        return wrappedFile.compareTo(java.io.File(other.getAbsolutePath()))
    }

    actual override fun equals(other: Any?): Boolean {
        return other is PlatformFile && wrappedFile.absolutePath == other.getAbsolutePath()
    }
}