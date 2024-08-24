package dev.zwander.kotlin.file.filekit

import dev.zwander.kotlin.file.IPlatformFile
import io.github.vinceglb.filekit.core.PlatformDirectory
import io.github.vinceglb.filekit.core.PlatformFile

expect fun PlatformFile.toKmpFile(): IPlatformFile
expect fun PlatformDirectory.toKmpFile(): IPlatformFile
