package dev.zwander.kotlin.file.attribute

/**
 * This class consists exclusively of static methods that operate on sets of
 * {@link PosixFilePermission} objects.
 *
 * @since 1.7
 */

@Suppress("unused")
object PosixFilePermissions {
    const val OWNER_READ_FILEMODE: Int = 256
    const val OWNER_WRITE_FILEMODE: Int = 128
    const val OWNER_EXEC_FILEMODE: Int = 64
    const val GROUP_READ_FILEMODE: Int = 32
    const val GROUP_WRITE_FILEMODE: Int = 16
    const val GROUP_EXEC_FILEMODE: Int = 8
    const val OTHERS_READ_FILEMODE: Int = 4
    const val OTHERS_WRITE_FILEMODE: Int = 2
    const val OTHERS_EXEC_FILEMODE: Int = 1

    // Write string representation of permission bits to {@code sb}.
    private fun writeBits(sb: StringBuilder, r: Boolean, w: Boolean, x: Boolean) {
        if (r) {
            sb.append('r')
        } else {
            sb.append('-')
        }
        if (w) {
            sb.append('w')
        } else {
            sb.append('-')
        }
        if (x) {
            sb.append('x')
        } else {
            sb.append('-')
        }
    }

    /**
     * Returns the `String` representation of a set of permissions. It
     * is guaranteed that the returned `String` can be parsed by the
     * [.fromString] method.
     *
     *
     *  If the set contains `null` or elements that are not of type
     * `PosixFilePermission` then these elements are ignored.
     *
     * @param   perms
     * the set of permissions
     *
     * @return  the string representation of the permission set
     */
    fun toString(perms: Set<PosixFilePermission?>): String {
        val sb = StringBuilder(9)
        writeBits(
            sb, perms.contains(PosixFilePermission.OWNER_READ), perms.contains(PosixFilePermission.OWNER_WRITE),
            perms.contains(PosixFilePermission.OWNER_EXECUTE)
        )
        writeBits(
            sb, perms.contains(PosixFilePermission.GROUP_READ), perms.contains(PosixFilePermission.GROUP_WRITE),
            perms.contains(PosixFilePermission.GROUP_EXECUTE)
        )
        writeBits(
            sb, perms.contains(PosixFilePermission.OTHERS_READ), perms.contains(PosixFilePermission.OTHERS_WRITE),
            perms.contains(PosixFilePermission.OTHERS_EXECUTE)
        )
        return sb.toString()
    }

    private fun isSet(c: Char, setValue: Char): Boolean {
        if (c == setValue) return true
        if (c == '-') return false
        throw IllegalArgumentException("Invalid mode")
    }

    private fun isR(c: Char): Boolean {
        return isSet(c, 'r')
    }

    private fun isW(c: Char): Boolean {
        return isSet(c, 'w')
    }

    private fun isX(c: Char): Boolean {
        return isSet(c, 'x')
    }

    /**
     * Returns the set of permissions corresponding to a given `String`
     * representation.
     *
     *
     *  The `perms` parameter is a `String` representing the
     * permissions. It has 9 characters that are interpreted as three sets of
     * three. The first set refers to the owner's permissions; the next to the
     * group permissions and the last to others. Within each set, the first
     * character is `'r'` to indicate permission to read, the second
     * character is `'w'` to indicate permission to write, and the third
     * character is `'x'` for execute permission. Where a permission is
     * not set then the corresponding character is set to `'-'`.
     *
     *
     *  **Usage Example:**
     * Suppose we require the set of permissions that indicate the owner has read,
     * write, and execute permissions, the group has read and execute permissions
     * and others have none.
     * <pre>
     * Set&lt;PosixFilePermission&gt; perms = PosixFilePermissions.fromString("rwxr-x---");
    </pre> *
     *
     * @param   perms
     * string representing a set of permissions
     *
     * @return  the resulting set of permissions
     *
     * @throws  IllegalArgumentException
     * if the string cannot be converted to a set of permissions
     *
     * @see .toString
     */
    fun fromString(perms: String): Set<PosixFilePermission> {
        require(perms.length == 9) { "Invalid mode" }
        val result: MutableSet<PosixFilePermission> = mutableSetOf()
        if (isR(perms[0])) result.add(PosixFilePermission.OWNER_READ)
        if (isW(perms[1])) result.add(PosixFilePermission.OWNER_WRITE)
        if (isX(perms[2])) result.add(PosixFilePermission.OWNER_EXECUTE)
        if (isR(perms[3])) result.add(PosixFilePermission.GROUP_READ)
        if (isW(perms[4])) result.add(PosixFilePermission.GROUP_WRITE)
        if (isX(perms[5])) result.add(PosixFilePermission.GROUP_EXECUTE)
        if (isR(perms[6])) result.add(PosixFilePermission.OTHERS_READ)
        if (isW(perms[7])) result.add(PosixFilePermission.OTHERS_WRITE)
        if (isX(perms[8])) result.add(PosixFilePermission.OTHERS_EXECUTE)
        return result
    }

    /**
     * Creates a [FileAttribute], encapsulating a copy of the given file
     * permissions.
     *
     * @param   perms
     * the set of permissions
     *
     * @return  an attribute encapsulating the given file permissions with
     * [name][FileAttribute.name] `"posix:permissions"`
     *
     * @throws  ClassCastException
     * if the set contains elements that are not of type `PosixFilePermission`
     */
    fun asFileAttribute(perms: Set<PosixFilePermission?>): FileAttribute<Set<PosixFilePermission>> {
        // copy set and check for nulls (CCE will be thrown if an element is not
        // a PosixFilePermission)
        if (perms.any { it == null }) {
            throw NullPointerException()
        }
        val value: Set<PosixFilePermission> = HashSet(perms.filterNotNull())
        return object : FileAttribute<Set<PosixFilePermission>> {
            override fun name(): String {
                return "posix:permissions"
            }

            override fun value(): Set<PosixFilePermission> {
                return value.toSet()
            }
        }
    }

    fun posixFilePermissions(mode: Int): Set<PosixFilePermission> {
        var mask = 1
        val perms: MutableSet<PosixFilePermission> = mutableSetOf()
        for (flag in posixFilePermissionReverse) {
            if ((mask and mode) != 0) {
                perms.add(flag)
            }
            mask = mask shl 1
        }
        return perms
    }

    fun toOctalFileMode(permissions: Set<PosixFilePermission>): Int {
        var result = 0
        for (permissionBit in permissions) {
            result = when (permissionBit) {
                PosixFilePermission.OWNER_READ -> result or OWNER_READ_FILEMODE
                PosixFilePermission.OWNER_WRITE -> result or OWNER_WRITE_FILEMODE
                PosixFilePermission.OWNER_EXECUTE -> result or OWNER_EXEC_FILEMODE
                PosixFilePermission.GROUP_READ -> result or GROUP_READ_FILEMODE
                PosixFilePermission.GROUP_WRITE -> result or GROUP_WRITE_FILEMODE
                PosixFilePermission.GROUP_EXECUTE -> result or GROUP_EXEC_FILEMODE
                PosixFilePermission.OTHERS_READ -> result or OTHERS_READ_FILEMODE
                PosixFilePermission.OTHERS_WRITE -> result or OTHERS_WRITE_FILEMODE
                PosixFilePermission.OTHERS_EXECUTE -> result or OTHERS_EXEC_FILEMODE
            }
        }
        return result
    }
}
