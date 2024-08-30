package dev.zwander.kotlin.file

import android.net.Uri
import java.io.File

@Suppress("unused")
actual object FileUtils {
    actual fun fromString(input: String, isDirectory: Boolean): IPlatformFile? {
        val uri = Uri.parse(input)

        return when {
            uri.scheme == null -> {
                null
            }
            uri.scheme != "file" -> {
                PlatformUriFile(ContextInitializer.appContext, uri, isDirectory)
            }
            uri.scheme == "file" -> {
                uri.path?.let {
                    PlatformFile(it)
                }
            }
            else -> {
                PlatformFile(File(input))
            }
        }
    }
}