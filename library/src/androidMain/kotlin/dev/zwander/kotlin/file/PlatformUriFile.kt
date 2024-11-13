package dev.zwander.kotlin.file

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import kotlinx.io.*

@Suppress("unused")
class PlatformUriFile(
    private val context: Context,
    private val wrappedFile: DocumentFile,
) : IPlatformFile {
    @Suppress("unused")
    constructor(context: Context, file: IPlatformFile) : this(context, (file as PlatformUriFile).wrappedFile)

    @Suppress("unused")
    constructor(context: Context, uri: Uri, isTree: Boolean) : this(
        context,
        if (isTree) {
            DocumentFile.fromTreeUri(context, uri)
        } else {
            DocumentFile.fromSingleUri(context, uri)
        }!!,
    )

    override val nameWithoutExtension: String
        get() = wrappedFile.name?.substringBeforeLast(".") ?: wrappedFile.uri.toString()

    override fun getName(): String = wrappedFile.name ?: wrappedFile.uri.toString()
    override fun getParent(): String? = wrappedFile.parentFile?.uri?.toString()
    override fun getParentFile(): IPlatformFile? = wrappedFile.parentFile?.let { PlatformUriFile(context, it) }
    override fun getPath(): String = wrappedFile.uri.toString()
    override fun isAbsolute(): Boolean = false
    override fun getAbsolutePath(): String = getPath()
    override fun getAbsoluteFile(): IPlatformFile = this
    override fun getCanonicalPath(): String = throw IllegalStateException("Not Supported")
    override fun getCanonicalFile(): IPlatformFile = throw IllegalStateException("Not Supported")
    override fun getCanRead(): Boolean = wrappedFile.canRead()
    override fun getCanWrite(): Boolean = wrappedFile.canWrite()
    override fun getExists(): Boolean = wrappedFile.exists()
    override fun isDirectory(): Boolean = wrappedFile.isDirectory
    override fun isFile(): Boolean = wrappedFile.isFile
    override fun isHidden(): Boolean = false
    override fun getLastModified(): Long = wrappedFile.lastModified()
    override fun getLength(): Long = wrappedFile.length()
    override fun getTotalSpace(): Long = throw IllegalStateException("Not Supported")
    override fun getFreeSpace(): Long = throw IllegalStateException("Not Supported")
    override fun getUsableSpace(): Long = throw IllegalStateException("Not Supported")

    override fun createNewFile(): Boolean {
        //DocumentFile creates itself.
        return true
    }

    override fun delete(): Boolean {
        return wrappedFile.delete()
    }

    override fun deleteOnExit() {
        throw IllegalStateException("Not Supported")
    }

    override fun list(): Array<String> {
        return wrappedFile.listFiles().map { it.name ?: it.uri.toString() }.toTypedArray()
    }

    override fun list(filter: (dir: IPlatformFile, name: String) -> Boolean): Array<String> {
        return wrappedFile.listFiles().filter {
            filter(PlatformUriFile(context, it.parentFile!!), it.name ?: it.uri.toString())
        }.map { it.name ?: it.uri.toString() }.toTypedArray()
    }

    override fun listFiles(): Array<IPlatformFile> {
        return wrappedFile.listFiles().map { PlatformUriFile(context, it) }
            .toTypedArray()
    }

    override fun listFiles(filter: (dir: IPlatformFile, name: String) -> Boolean): Array<IPlatformFile> {
        return wrappedFile.listFiles().filter { filter(PlatformUriFile(context, it.parentFile!!), it.name ?: it.uri.toString()) }
            .map { PlatformUriFile(context, it) }
            .toTypedArray()
    }

    override fun listFiles(filter: (pathName: IPlatformFile) -> Boolean): Array<IPlatformFile> {
        return wrappedFile.listFiles().filter { filter(PlatformUriFile(context, it)) }
            .map { PlatformUriFile(context, it) }
            .toTypedArray()
    }

    override fun mkdir(): Boolean {
        return true
    }

    override fun mkdirs(): Boolean {
        return true
    }

    override fun renameTo(dest: IPlatformFile): Boolean {
        return wrappedFile.renameTo(dest.getName())
    }

    override fun setLastModified(time: Long): Boolean {
        throw IllegalStateException("Not Supported")
    }

    override fun setReadOnly(): Boolean {
        throw IllegalStateException("Not Supported")
    }

    override fun setWritable(writable: Boolean, ownerOnly: Boolean): Boolean {
        throw IllegalStateException("Not Supported")
    }

    override fun setWritable(writable: Boolean): Boolean {
        throw IllegalStateException("Not Supported")
    }

    override fun setReadable(readable: Boolean, ownerOnly: Boolean): Boolean {
        throw IllegalStateException("Not Supported")
    }

    override fun setReadable(readable: Boolean): Boolean {
        throw IllegalStateException("Not Supported")
    }

    override fun setExecutable(executable: Boolean, ownerOnly: Boolean): Boolean {
        throw IllegalStateException("Not Supported")
    }

    override fun setExecutable(executable: Boolean): Boolean {
        throw IllegalStateException("Not Supported")
    }

    override fun canExecute(): Boolean {
        throw IllegalStateException("Not Supported")
    }

    override fun openOutputStream(append: Boolean): Sink? {
        return context.contentResolver.openOutputStream(wrappedFile.uri, "w${if (append) "a" else ""}")?.asSink()?.buffered()
    }

    override fun openInputStream(): Source? {
        return context.contentResolver.openInputStream(wrappedFile.uri)?.asSource()?.buffered()
    }

    override fun child(childName: String, isDirectory: Boolean, mimeType: String): IPlatformFile? {
        return if (isDirectory()) {
            if (isDirectory) {
                (wrappedFile.findFile(childName) ?: wrappedFile.createDirectory(childName))?.let {
                    PlatformUriFile(context, it)
                }
            } else {
                (wrappedFile.findFile(childName) ?: wrappedFile.createFile(mimeType, childName))?.let {
                    PlatformUriFile(context, it)
                }
            }
        } else {
            null
        }
    }

    override fun compareTo(other: IPlatformFile): Int {
        if (other !is PlatformUriFile) return -1

        return wrappedFile.uri.compareTo(other.wrappedFile.uri)
    }

    override fun equals(other: Any?): Boolean {
        return other is PlatformUriFile
                && wrappedFile.uri == other.wrappedFile.uri
    }

    override fun hashCode(): Int {
        return wrappedFile.uri.hashCode()
    }

    override fun toString(): String = stringify()
}
