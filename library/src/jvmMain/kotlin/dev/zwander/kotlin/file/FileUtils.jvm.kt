package dev.zwander.kotlin.file

import java.io.File
import java.net.URI
import java.net.URISyntaxException
import kotlin.io.path.toPath

@Suppress("unused")
actual object FileUtils {
    actual fun fromString(input: String, isDirectory: Boolean): IPlatformFile? {
        return try {
            val uri = URI(input)

            when (uri.scheme) {
                "file" -> {
                    PlatformFile(uri.toPath().toFile())
                }
                null -> {
                    PlatformFile(File(input))
                }
                else -> {
                    null
                }
            }
        } catch (e: URISyntaxException) {
            PlatformFile(File(input))
        }
    }
}