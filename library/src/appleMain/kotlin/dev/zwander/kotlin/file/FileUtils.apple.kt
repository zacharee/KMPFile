package dev.zwander.kotlin.file

import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.stringByExpandingTildeInPath

@Suppress("unused")
actual object FileUtils {
    actual fun fromString(input: String, isDirectory: Boolean): IPlatformFile? {
        @Suppress("CAST_NEVER_SUCCEEDS")
        val realPath = if (input.startsWith("~")) {
            (input as NSString).stringByExpandingTildeInPath
        } else {
            input
        }

        return PlatformFile(NSURL.fileURLWithPath(realPath, isDirectory))
    }
}