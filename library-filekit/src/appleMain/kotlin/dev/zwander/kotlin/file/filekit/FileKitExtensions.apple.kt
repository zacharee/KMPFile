package dev.zwander.kotlin.file.filekit

import dev.zwander.kotlin.file.IPlatformFile
import io.github.vinceglb.filekit.core.PlatformDirectory
import io.github.vinceglb.filekit.core.PlatformFile

actual fun PlatformFile.toKmpFile(): IPlatformFile {
    return dev.zwander.kotlin.file.PlatformFile(nsUrl)
}

actual fun PlatformDirectory.toKmpFile(): IPlatformFile {
    return dev.zwander.kotlin.file.PlatformFile(nsUrl)
}
