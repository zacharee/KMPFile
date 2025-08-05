package dev.zwander.kotlin.file.filekit

import dev.zwander.kotlin.file.IPlatformFile
import io.github.vinceglb.filekit.PlatformFile

expect fun PlatformFile.toKmpFile(): IPlatformFile
