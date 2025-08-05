package dev.zwander.kotlin.file.filekit

import dev.zwander.kotlin.file.ContextInitializer
import dev.zwander.kotlin.file.IPlatformFile
import dev.zwander.kotlin.file.PlatformUriFile
import io.github.vinceglb.filekit.AndroidFile
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.isDirectory

@Suppress("unused")
actual fun PlatformFile.toKmpFile(): IPlatformFile {
    val wrappedFile = androidFile

    return when (wrappedFile) {
        is AndroidFile.FileWrapper -> {
            dev.zwander.kotlin.file.PlatformFile(wrappedFile.file)
        }
        is AndroidFile.UriWrapper -> {
            PlatformUriFile(
                context = ContextInitializer.appContext,
                uri = wrappedFile.uri,
                isTree = isDirectory(),
            )
        }
    }
}
