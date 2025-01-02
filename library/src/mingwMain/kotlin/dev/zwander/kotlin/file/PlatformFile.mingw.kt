@file:OptIn(ExperimentalForeignApi::class)

package dev.zwander.kotlin.file

import dev.zwander.kotlin.file.attribute.PosixFilePermission
import dev.zwander.kotlin.file.attribute.PosixFilePermissions
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.cValue
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlinx.cinterop.usePinned
import kotlinx.io.Buffer
import kotlinx.io.IOException
import kotlinx.io.RawSink
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.UnsafeIoApi
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.unsafe.UnsafeBufferOperations
import platform.posix.R_OK
import platform.posix.S_IRGRP
import platform.posix.S_IROTH
import platform.posix.S_IRWXU
import platform.posix.S_IXGRP
import platform.posix.S_IXOTH
import platform.posix.W_OK
import platform.posix.X_OK
import platform.posix.access
import platform.posix.chmod
import platform.posix.creat
import platform.posix.fclose
import platform.posix.ferror
import platform.posix.fflush
import platform.posix.fopen
import platform.posix.fwrite
import platform.posix.posix_errno
import platform.posix.stat
import platform.posix.strerror
import platform.posix.uint8_tVar
import platform.posix.utimbuf
import platform.posix.utime
import platform.windows.FILE_ATTRIBUTE_HIDDEN
import platform.windows.FILE_ATTRIBUTE_SYSTEM
import platform.windows.GetFileAttributesExA
import platform.windows.WIN32_FILE_ATTRIBUTE_DATA
import platform.windows._GET_FILEEX_INFO_LEVELS

@Suppress("unused", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual open class PlatformFile : IPlatformFile {
    actual companion object;

    private val wrappedPath: Path

    actual override val nameWithoutExtension: String
        get() = getName().substringBeforeLast(".")

    actual constructor(pathName: String) {
        wrappedPath = Path(pathName)
    }

    actual constructor(parent: String, child: String) {
        wrappedPath = Path("$parent/$child")
    }

    actual constructor(parent: PlatformFile, child: String) {
        wrappedPath = Path("${parent.getAbsolutePath()}/$child")
    }

    actual constructor(realFile: RealFile) {
        wrappedPath = realFile
    }

    actual override fun getName(): String {
        return wrappedPath.name
    }

    actual override fun getParent(): String? {
        return wrappedPath.parent?.toString()?.let { SystemFileSystem.resolve(Path(it)).toString() }
    }

    actual override fun getParentFile(): IPlatformFile? {
        return getParent()?.let { PlatformFile(it) }
    }

    actual override fun getPath(): String {
        return wrappedPath.toString()
    }

    actual override fun isAbsolute(): Boolean {
        return wrappedPath.isAbsolute
    }

    actual override fun getAbsolutePath(): String {
        return SystemFileSystem.resolve(wrappedPath).toString()
    }

    actual override fun getAbsoluteFile(): IPlatformFile {
        return PlatformFile(getAbsolutePath())
    }

    actual override fun getCanonicalPath(): String {
        return getAbsolutePath()
    }

    actual override fun getCanonicalFile(): IPlatformFile {
        return getAbsoluteFile()
    }

    actual override fun getCanRead(): Boolean {
        return access(wrappedPath.toString(), R_OK) == 0
    }

    actual override fun getCanWrite(): Boolean {
        return access(wrappedPath.toString(), W_OK) == 0
    }

    actual override fun getExists(): Boolean {
        return SystemFileSystem.exists(wrappedPath)
    }

    actual override fun isDirectory(): Boolean = SystemFileSystem.metadataOrNull(wrappedPath)?.isDirectory == true

    actual override fun isFile(): Boolean = SystemFileSystem.metadataOrNull(wrappedPath)?.isRegularFile == true

    actual override fun isHidden(): Boolean = memScoped {
        val info = alloc<WIN32_FILE_ATTRIBUTE_DATA>()
        GetFileAttributesExA(
            wrappedPath.toString(),
            _GET_FILEEX_INFO_LEVELS.GetFileExInfoStandard,
            info.ptr,
        )

        return (info.ptr.pointed.dwFileAttributes and FILE_ATTRIBUTE_HIDDEN.toUInt()) != 0U ||
                (info.ptr.pointed.dwFileAttributes and FILE_ATTRIBUTE_SYSTEM.toUInt()) != 0U
    }

    actual override fun getLastModified(): Long = memScoped {
        val info = alloc<WIN32_FILE_ATTRIBUTE_DATA>()
        GetFileAttributesExA(
            wrappedPath.toString(),
            _GET_FILEEX_INFO_LEVELS.GetFileExInfoStandard,
            info.ptr,
        )

        val writeTime = info.ptr.pointed.ftLastWriteTime.ptr.pointed
        val writeTimeMillis = (writeTime.dwLowDateTime.toLong() + (writeTime.dwHighDateTime.toLong() shl 32)) / 1000L

        return writeTimeMillis
    }

    actual override fun getLength(): Long = SystemFileSystem.metadataOrNull(wrappedPath)?.size ?: -1

    actual override fun getTotalSpace(): Long {
        error("Unsupported")
    }

    actual override fun getFreeSpace(): Long {
        error("Unsupported")
    }

    actual override fun getUsableSpace(): Long {
        error("Unsupported")
    }

    actual override fun createNewFile(): Boolean {
        return creat(wrappedPath.toString(), S_IRWXU + S_IRGRP + S_IXGRP + S_IROTH + S_IXOTH) == 0
    }

    actual override fun delete(): Boolean {
        SystemFileSystem.delete(wrappedPath, true)
        return true
    }

    actual override fun deleteOnExit() {
        error("Unsupported")
    }

    actual override fun list(): Array<String>? {
        return SystemFileSystem.list(wrappedPath).map { it.toString() }.toTypedArray()
    }

    actual override fun list(filter: (dir: IPlatformFile, name: String) -> Boolean): Array<String>? {
        return list()?.filter { filter(this, Path(it).name) }?.toTypedArray()
    }

    actual override fun listFiles(): Array<IPlatformFile>? {
        return list()?.map { PlatformFile(it) }?.toTypedArray()
    }

    actual override fun listFiles(filter: (dir: IPlatformFile, name: String) -> Boolean): Array<IPlatformFile>? {
        return listFiles()?.filter { filter(this, it.getName()) }?.toTypedArray()
    }

    actual override fun listFiles(filter: (pathName: IPlatformFile) -> Boolean): Array<IPlatformFile>? {
        return listFiles()?.filter(filter)?.toTypedArray()
    }

    actual override fun mkdir(): Boolean {
        return platform.posix.mkdir(wrappedPath.toString()) == 0
    }

    actual override fun mkdirs(): Boolean {
        SystemFileSystem.createDirectories(wrappedPath)
        return true
    }

    actual override fun renameTo(dest: IPlatformFile): Boolean {
        SystemFileSystem.atomicMove(wrappedPath, Path(dest.getAbsolutePath()))
        return true
    }

    actual override fun setLastModified(time: Long): Boolean {
        val times = cValue<utimbuf> { this.modtime = (time / 1000) }
        return utime(wrappedPath.toString(), times) == 0
    }

    actual override fun setReadOnly(): Boolean {
        return setReadable(readable = false, ownerOnly = false)
    }

    actual override fun setWritable(
        writable: Boolean,
        ownerOnly: Boolean,
    ): Boolean = memScoped {
        val stats = cValue<stat>()
        stat(wrappedPath.toString(), stats)

        val permissions = PosixFilePermissions.posixFilePermissions(stats.ptr.pointed.st_mode.toInt()).toMutableSet()

        if (writable) {
            permissions.add(PosixFilePermission.OWNER_WRITE)

            if (!ownerOnly) {
                permissions.add(PosixFilePermission.GROUP_WRITE)
                permissions.add(PosixFilePermission.OTHERS_WRITE)
            }
        } else {
            permissions.remove(PosixFilePermission.OWNER_WRITE)

            if (!ownerOnly) {
                permissions.remove(PosixFilePermission.GROUP_WRITE)
                permissions.remove(PosixFilePermission.OTHERS_WRITE)
            }
        }

        val newMode = PosixFilePermissions.toOctalFileMode(permissions)

        return chmod(wrappedPath.toString(), newMode) == 0
    }

    actual override fun setWritable(writable: Boolean): Boolean {
        return setWritable(writable, true)
    }

    actual override fun setReadable(
        readable: Boolean,
        ownerOnly: Boolean,
    ): Boolean = memScoped {
        val stats = cValue<stat>()
        stat(wrappedPath.toString(), stats)

        val permissions = PosixFilePermissions.posixFilePermissions(stats.ptr.pointed.st_mode.toInt()).toMutableSet()

        if (readable) {
            permissions.add(PosixFilePermission.OWNER_READ)

            if (!ownerOnly) {
                permissions.add(PosixFilePermission.GROUP_READ)
                permissions.add(PosixFilePermission.OTHERS_READ)
            }
        } else {
            permissions.remove(PosixFilePermission.OWNER_READ)

            if (!ownerOnly) {
                permissions.remove(PosixFilePermission.GROUP_READ)
                permissions.remove(PosixFilePermission.OTHERS_READ)
            }
        }

        val newMode = PosixFilePermissions.toOctalFileMode(permissions)

        return chmod(wrappedPath.toString(), newMode) == 0
    }

    actual override fun setReadable(readable: Boolean): Boolean {
        return setReadable(readable, true)
    }

    actual override fun setExecutable(
        executable: Boolean,
        ownerOnly: Boolean,
    ): Boolean = memScoped {
        val stats = cValue<stat>()
        stat(wrappedPath.toString(), stats)

        val permissions = PosixFilePermissions.posixFilePermissions(stats.ptr.pointed.st_mode.toInt()).toMutableSet()

        if (executable) {
            permissions.add(PosixFilePermission.OWNER_EXECUTE)

            if (!ownerOnly) {
                permissions.add(PosixFilePermission.GROUP_EXECUTE)
                permissions.add(PosixFilePermission.OTHERS_EXECUTE)
            }
        } else {
            permissions.remove(PosixFilePermission.OWNER_EXECUTE)

            if (!ownerOnly) {
                permissions.remove(PosixFilePermission.GROUP_EXECUTE)
                permissions.remove(PosixFilePermission.OTHERS_EXECUTE)
            }
        }

        val newMode = PosixFilePermissions.toOctalFileMode(permissions)

        return chmod(wrappedPath.toString(), newMode) == 0
    }

    actual override fun setExecutable(executable: Boolean): Boolean {
        return setExecutable(executable, true)
    }

    actual override fun canExecute(): Boolean {
        return access(wrappedPath.toString(), X_OK) == 0
    }

    @OptIn(UnsafeIoApi::class)
    actual override fun openOutputStream(append: Boolean, truncate: Boolean): Sink? = memScoped {
        enforceWriteMode(append, truncate)

        // https://github.com/Archinamon/native-file-io
        if (!append && !truncate) {
            val fd = fopen(getAbsolutePath(), "w")

            return (object : RawSink {
                override fun close() {
                    fclose(fd).ensureUnixCallResult("fclose") { it == 0 }
                }

                override fun flush() {
                    fflush(fd).ensureUnixCallResult("fflush") { it == 0 }
                }

                override fun write(source: Buffer, byteCount: Long) {
                    var remaining = byteCount
                    var bytesWritten = 0L
                    while (remaining > 0) {
                        UnsafeBufferOperations.readFromHead(source) { data, pos, limit ->
                            val toCopy = minOf(remaining, (limit - pos).toLong()).toInt()
                            bytesWritten = data.usePinned {
                                val bytes = it.addressOf(pos).reinterpret<uint8_tVar>()
                                fwrite(bytes, 1U, toCopy.toULong(), fd)
                            }.toLong()

                            if (bytesWritten != toCopy.toLong()) {
                                ferror(fd).ensureUnixCallResult("fwrite") { it == 0 }
                            }
                            0
                        }

                        if (bytesWritten == 0L) throw IOException("NSOutputStream reached capacity")

                        source.skip(bytesWritten)
                        remaining -= bytesWritten
                    }
                }

            }).buffered()
        }

        return SystemFileSystem.sink(wrappedPath, append).buffered()
    }

    actual override fun openInputStream(): Source? {
        return SystemFileSystem.source(wrappedPath).buffered()
    }

    actual override fun child(
        childName: String,
        isDirectory: Boolean,
        mimeType: String
    ): IPlatformFile? {
        return if (isDirectory()) {
            PlatformFile(this, childName)
        } else {
            null
        }
    }

    actual override fun hashCode(): Int = wrappedPath.hashCode()

    actual override fun equals(other: Any?): Boolean = other is PlatformFile && other.wrappedPath == wrappedPath

    actual override fun compareTo(other: IPlatformFile): Int = getName().compareTo(other.getName())

    actual override fun toString(): String = stringify()

    /* =========== */
    /* https://github.com/Archinamon/native-file-io/blob/master/src/posixMain/kotlin/posix.kt */

    private inline fun Int.ensureUnixCallResult(op: String, predicate: (Int) -> Boolean): Int {
        if (!predicate(this)) {
            throw Error("$op: ${strerror(posix_errno())!!.toKString()}")
        }
        return this
    }

    private inline fun Long.ensureUnixCallResult(op: String, predicate: (Long) -> Boolean): Long {
        if (!predicate(this)) {
            throw Error("$op: ${strerror(posix_errno())!!.toKString()}")
        }
        return this
    }

    private inline fun ULong.ensureUnixCallResult(op: String, predicate: (ULong) -> Boolean): ULong {
        if (!predicate(this)) {
            throw Error("$op: ${strerror(posix_errno())!!.toKString()}")
        }
        return this
    }
}