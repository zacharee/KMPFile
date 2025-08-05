package dev.zwander.kotlin.file.filekit

import dev.zwander.kotlin.file.IPlatformFile
import io.github.vinceglb.filekit.PlatformFile

actual fun PlatformFile.toKmpFile(): IPlatformFile {
    return dev.zwander.kotlin.file.PlatformFile(file)
}
