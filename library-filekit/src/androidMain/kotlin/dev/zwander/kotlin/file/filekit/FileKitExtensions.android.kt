package dev.zwander.kotlin.file.filekit

import dev.zwander.kotlin.file.IPlatformFile
import dev.zwander.kotlin.file.PlatformUriFile
import io.github.vinceglb.filekit.core.PlatformDirectory
import io.github.vinceglb.filekit.core.PlatformFile

actual fun PlatformFile.toKmpFile(): IPlatformFile {
    return PlatformUriFile(
        context = ContextInitializer.appContext,
        uri = uri,
        isTree = false,
    )
}

actual fun PlatformDirectory.toKmpFile(): IPlatformFile {
    return PlatformUriFile(
        context = ContextInitializer.appContext,
        uri = uri,
        isTree = true,
    )
}
