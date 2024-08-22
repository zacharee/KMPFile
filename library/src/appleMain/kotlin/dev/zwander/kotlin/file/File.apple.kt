package dev.zwander.kotlin.file

import dev.zwander.kotlin.file.attribute.PosixFilePermission
import dev.zwander.kotlin.file.attribute.PosixFilePermissions
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemPathSeparator
import platform.Foundation.NSDate
import platform.Foundation.NSError
import platform.Foundation.NSFileImmutable
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileModificationDate
import platform.Foundation.NSFilePosixPermissions
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSURLAttributeModificationDateKey
import platform.Foundation.NSURLFileSizeKey
import platform.Foundation.NSURLVolumeAvailableCapacityForImportantUsageKey
import platform.Foundation.NSURLVolumeAvailableCapacityKey
import platform.Foundation.NSURLVolumeTotalCapacityKey
import platform.Foundation.create
import platform.Foundation.lastPathComponent
import platform.Foundation.timeIntervalSince1970

/**
 * Platforms should actuate this class to implement
 * various filesystem classes they use.
 */
actual open class PlatformFile : IPlatformFile {
    actual companion object;

    private val nsUrl: NSURL

    actual constructor(pathName: String) {
        nsUrl = NSURL.fileURLWithPath(pathName)
    }

    actual constructor(parent: String, child: String) {
        nsUrl = NSURL.fileURLWithPath("$parent${SystemPathSeparator}${child}")
    }

    actual constructor(parent: PlatformFile, child: String) {
        nsUrl = NSURL.fileURLWithPath("${parent.getAbsolutePath()}${SystemPathSeparator}${child}")
    }

    @Suppress("unused")
    constructor(url: NSURL) {
        this.nsUrl = url
    }

    actual override val nameWithoutExtension: String
        get() = getName().substringBeforeLast(".")

    actual override fun getName(): String = nsUrl.lastPathComponent ?: ""

    actual override suspend fun getParent(): String? = "${Path(getAbsolutePath()).parent?.toString()}${SystemPathSeparator}"

    actual override suspend fun getParentFile(): IPlatformFile? = getParent()?.let { PlatformFile(it) }

    actual override suspend fun isAbsolute(): Boolean = nsUrl.path == nsUrl.relativePath

    actual override fun getPath(): String = nsUrl.relativePath ?: ""

    actual override fun getAbsolutePath(): String = nsUrl.path ?: ""

    actual override fun getAbsoluteFile(): IPlatformFile = PlatformFile(getAbsolutePath())

    actual override suspend fun getCanonicalPath(): String = getAbsolutePath()

    actual override suspend fun getCanonicalFile(): IPlatformFile = getAbsoluteFile()

    actual override suspend fun getCanRead(): Boolean = NSFileManager.defaultManager.isReadableFileAtPath(getAbsolutePath())

    actual override suspend fun getCanWrite(): Boolean = NSFileManager.defaultManager.isWritableFileAtPath(getAbsolutePath())

    actual override suspend fun getExists(): Boolean = NSFileManager.defaultManager.fileExistsAtPath(getAbsolutePath())

    actual override suspend fun isDirectory(): Boolean = nsUrl.hasDirectoryPath

    actual override suspend fun isFile(): Boolean = !nsUrl.fileURL

    actual override suspend fun isHidden(): Boolean = false

    @OptIn(ExperimentalForeignApi::class)
    actual override suspend fun getLastModified(): Long = memScoped {
        nsUrl.path?.let {
            val errorPointer: CPointer<ObjCObjectVar<NSError?>> =
                alloc<ObjCObjectVar<NSError?>>().ptr
            (NSFileManager.defaultManager.attributesOfItemAtPath(it, errorPointer)?.get(NSURLAttributeModificationDateKey) as NSDate?)?.timeIntervalSince1970?.toLong()
        } ?: 0
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual override suspend fun getLength(): Long = memScoped {
        val valuePointer: CPointer<ObjCObjectVar<Any?>> = alloc<ObjCObjectVar<Any?>>().ptr
        val errorPointer: CPointer<ObjCObjectVar<NSError?>> =
            alloc<ObjCObjectVar<NSError?>>().ptr
        nsUrl.getResourceValue(valuePointer, NSURLFileSizeKey, errorPointer)
        return valuePointer.pointed.value as? Long? ?: 0
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual override suspend fun getTotalSpace(): Long  = memScoped {
        val valuePointer: CPointer<ObjCObjectVar<Any?>> = alloc<ObjCObjectVar<Any?>>().ptr
        val errorPointer: CPointer<ObjCObjectVar<NSError?>> =
            alloc<ObjCObjectVar<NSError?>>().ptr
        nsUrl.getResourceValue(valuePointer, NSURLVolumeTotalCapacityKey, errorPointer)
        return valuePointer.pointed.value as? Long ?: 0
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual override suspend fun getFreeSpace(): Long = memScoped {
        val valuePointer: CPointer<ObjCObjectVar<Any?>> = alloc<ObjCObjectVar<Any?>>().ptr
        val errorPointer: CPointer<ObjCObjectVar<NSError?>> =
            alloc<ObjCObjectVar<NSError?>>().ptr
        nsUrl.getResourceValue(valuePointer, NSURLVolumeAvailableCapacityKey, errorPointer)
        return valuePointer.pointed.value as? Long ?: 0
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual override suspend fun getUsableSpace(): Long = memScoped {
        val valuePointer: CPointer<ObjCObjectVar<Any?>> = alloc<ObjCObjectVar<Any?>>().ptr
        val errorPointer: CPointer<ObjCObjectVar<NSError?>> =
            alloc<ObjCObjectVar<NSError?>>().ptr
        nsUrl.getResourceValue(valuePointer, NSURLVolumeAvailableCapacityForImportantUsageKey, errorPointer)
        return valuePointer.pointed.value as? Long ?: 0
    }

    actual override suspend fun createNewFile(): Boolean = NSFileManager.defaultManager.createFileAtPath(getAbsolutePath(), null, null)

    @OptIn(ExperimentalForeignApi::class)
    actual override suspend fun delete(): Boolean = memScoped {
        val errorPointer: CPointer<ObjCObjectVar<NSError?>> =
            alloc<ObjCObjectVar<NSError?>>().ptr
        NSFileManager.defaultManager.removeItemAtURL(nsUrl, errorPointer)
    }

    actual override suspend fun deleteOnExit() {}

    @OptIn(ExperimentalForeignApi::class)
    actual override suspend fun list(): Array<String>? = memScoped {
        val errorPointer: CPointer<ObjCObjectVar<NSError?>> =
            alloc<ObjCObjectVar<NSError?>>().ptr
        NSFileManager.defaultManager.contentsOfDirectoryAtPath(getAbsolutePath(), errorPointer)?.map {
            val item = (it as NSString)
            "${getAbsolutePath()}/$item"
        }?.toTypedArray()
    }

    actual override suspend fun list(filter: (dir: IPlatformFile, name: String) -> Boolean): Array<String>? = list()?.filter { filter(getAbsoluteFile(), it) }?.toTypedArray()

    actual override suspend fun listFiles(): Array<IPlatformFile>? = list()?.map { PlatformFile(it) }?.toTypedArray()

    actual override suspend fun listFiles(filter: (dir: IPlatformFile, name: String) -> Boolean): Array<IPlatformFile>? = list(filter)?.map { PlatformFile(it) }?.toTypedArray()

    actual override suspend fun listFiles(filter: (pathName: IPlatformFile) -> Boolean): Array<IPlatformFile>? = listFiles()?.filter { filter(it) }?.toTypedArray()

    @OptIn(ExperimentalForeignApi::class)
    actual override suspend fun mkdir(): Boolean = memScoped {
        val errorPointer: CPointer<ObjCObjectVar<NSError?>> =
            alloc<ObjCObjectVar<NSError?>>().ptr
        NSFileManager.defaultManager.createDirectoryAtPath(getAbsolutePath(), false, null, errorPointer)
    }

    @OptIn(ExperimentalForeignApi::class)
    actual override suspend fun mkdirs(): Boolean = memScoped {
        val errorPointer: CPointer<ObjCObjectVar<NSError?>> =
            alloc<ObjCObjectVar<NSError?>>().ptr
        NSFileManager.defaultManager.createDirectoryAtPath(getAbsolutePath(), true, null, errorPointer)
    }

    @OptIn(ExperimentalForeignApi::class)
    actual override suspend fun renameTo(dest: IPlatformFile): Boolean = memScoped {
        val errorPointer: CPointer<ObjCObjectVar<NSError?>> =
            alloc<ObjCObjectVar<NSError?>>().ptr
        NSFileManager.defaultManager.moveItemAtPath(getAbsolutePath(), dest.getAbsolutePath(), errorPointer)
    }

    @Suppress("UnnecessaryOptInAnnotation")
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual override suspend fun setLastModified(time: Long): Boolean = memScoped {
        val errorPointer: CPointer<ObjCObjectVar<NSError?>> =
            alloc<ObjCObjectVar<NSError?>>().ptr
        NSFileManager.defaultManager.setAttributes(
            mapOf(NSFileModificationDate to NSDate.create(timeIntervalSince1970 = time.toDouble())),
            getAbsolutePath(),
            errorPointer,
        )
    }

    @OptIn(ExperimentalForeignApi::class)
    actual override suspend fun setReadOnly(): Boolean = memScoped {
        val errorPointer: CPointer<ObjCObjectVar<NSError?>> =
            alloc<ObjCObjectVar<NSError?>>().ptr
        NSFileManager.defaultManager.setAttributes(
            mapOf(NSFileImmutable to NSNumber(1)),
            getAbsolutePath(),
            errorPointer,
        )
    }

    @OptIn(ExperimentalForeignApi::class)
    actual override suspend fun setWritable(
        writable: Boolean,
        ownerOnly: Boolean,
    ): Boolean = memScoped {
        val currentPermissions = getPosixPermissions().toMutableSet()

        if (writable) {
            currentPermissions.add(PosixFilePermission.OWNER_WRITE)

            if (!ownerOnly) {
                currentPermissions.add(PosixFilePermission.GROUP_WRITE)
                currentPermissions.add(PosixFilePermission.OTHERS_WRITE)
            }
        } else {
            currentPermissions.remove(PosixFilePermission.OWNER_WRITE)
            currentPermissions.remove(PosixFilePermission.GROUP_WRITE)
            currentPermissions.remove(PosixFilePermission.OTHERS_WRITE)
        }

        val errorPointer: CPointer<ObjCObjectVar<NSError?>> =
            alloc<ObjCObjectVar<NSError?>>().ptr
        NSFileManager.defaultManager.setAttributes(
            mapOf(
                NSFileImmutable to NSNumber(if (writable) 0 else 1),
                NSFilePosixPermissions to NSNumber(PosixFilePermissions.toOctalFileMode(currentPermissions)),
            ),
            getAbsolutePath(),
            errorPointer,
        )
    }

    actual override suspend fun setWritable(writable: Boolean): Boolean = setWritable(writable, true)

    @OptIn(ExperimentalForeignApi::class)
    actual override suspend fun setReadable(
        readable: Boolean,
        ownerOnly: Boolean,
    ): Boolean = memScoped {
        val currentPermissions = getPosixPermissions().toMutableSet()

        if (readable) {
            currentPermissions.add(PosixFilePermission.OWNER_READ)

            if (!ownerOnly) {
                currentPermissions.add(PosixFilePermission.GROUP_READ)
                currentPermissions.add(PosixFilePermission.OTHERS_READ)
            }
        } else {
            currentPermissions.remove(PosixFilePermission.OWNER_READ)
            currentPermissions.remove(PosixFilePermission.GROUP_READ)
            currentPermissions.remove(PosixFilePermission.OTHERS_READ)
        }

        val errorPointer: CPointer<ObjCObjectVar<NSError?>> =
            alloc<ObjCObjectVar<NSError?>>().ptr
        NSFileManager.defaultManager.setAttributes(
            mapOf(
                NSFilePosixPermissions to NSNumber(PosixFilePermissions.toOctalFileMode(currentPermissions)),
            ),
            getAbsolutePath(),
            errorPointer,
        )
    }

    actual override suspend fun setReadable(readable: Boolean): Boolean = setReadable(readable, true)

    @OptIn(ExperimentalForeignApi::class)
    actual override suspend fun setExecutable(
        executable: Boolean,
        ownerOnly: Boolean,
    ): Boolean = memScoped {
        val currentPermissions = getPosixPermissions().toMutableSet()

        if (executable) {
            currentPermissions.add(PosixFilePermission.OWNER_EXECUTE)

            if (!ownerOnly) {
                currentPermissions.add(PosixFilePermission.GROUP_EXECUTE)
                currentPermissions.add(PosixFilePermission.OTHERS_EXECUTE)
            }
        } else {
            currentPermissions.remove(PosixFilePermission.OWNER_EXECUTE)
            currentPermissions.remove(PosixFilePermission.GROUP_EXECUTE)
            currentPermissions.remove(PosixFilePermission.OTHERS_EXECUTE)
        }

        val errorPointer: CPointer<ObjCObjectVar<NSError?>> =
            alloc<ObjCObjectVar<NSError?>>().ptr
        NSFileManager.defaultManager.setAttributes(
            mapOf(
                NSFilePosixPermissions to NSNumber(PosixFilePermissions.toOctalFileMode(currentPermissions)),
            ),
            getAbsolutePath(),
            errorPointer,
        )
    }

    actual override suspend fun setExecutable(executable: Boolean): Boolean = setExecutable(executable, true)

    actual override suspend fun canExecute(): Boolean = NSFileManager.defaultManager.isExecutableFileAtPath(getAbsolutePath())

    actual override suspend fun openOutputStream(append: Boolean): Sink? = SystemFileSystem.sink(Path(getAbsolutePath()), append).buffered()

    actual override suspend fun openInputStream(): Source? = SystemFileSystem.source(Path(getAbsolutePath())).buffered()

    actual override fun hashCode(): Int = nsUrl.hash().toInt()

    actual override fun equals(other: Any?): Boolean = other is PlatformFile && getAbsolutePath() == other.getAbsolutePath()

    actual override fun compareTo(other: IPlatformFile): Int = getAbsolutePath().compareTo(other.getAbsolutePath())

    @OptIn(ExperimentalForeignApi::class)
    private fun getPosixPermissions(): Set<PosixFilePermission> = memScoped {
        val errorPointer: CPointer<ObjCObjectVar<NSError?>> =
            alloc<ObjCObjectVar<NSError?>>().ptr
        val currentPermissions = (NSFileManager.defaultManager.attributesOfItemAtPath(getAbsolutePath(), errorPointer)
            ?.get(NSFilePosixPermissions) as? NSNumber)?.shortValue ?: throw IllegalStateException("Unable to retrieve permissions")

        PosixFilePermissions.posixFilePermissions(currentPermissions.toInt())
    }
}