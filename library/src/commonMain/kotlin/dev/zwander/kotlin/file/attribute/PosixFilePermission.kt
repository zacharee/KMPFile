package dev.zwander.kotlin.file.attribute

/**
 * Defines the bits for use with the [ permissions][PosixFileAttributes.permissions] attribute.
 *
 *
 *  The [PosixFilePermissions] class defines methods for manipulating
 * set of permissions.
 *
 * @since 1.7
 */
enum class PosixFilePermission {
    /**
     * Read permission, owner.
     */
    OWNER_READ,

    /**
     * Write permission, owner.
     */
    OWNER_WRITE,

    /**
     * Execute/search permission, owner.
     */
    OWNER_EXECUTE,

    /**
     * Read permission, group.
     */
    GROUP_READ,

    /**
     * Write permission, group.
     */
    GROUP_WRITE,

    /**
     * Execute/search permission, group.
     */
    GROUP_EXECUTE,

    /**
     * Read permission, others.
     */
    OTHERS_READ,

    /**
     * Write permission, others.
     */
    OTHERS_WRITE,

    /**
     * Execute/search permission, others.
     */
    OTHERS_EXECUTE
}

val posixFilePermissionReverse = arrayOf(
    PosixFilePermission.OTHERS_EXECUTE,
    PosixFilePermission.OTHERS_WRITE,
    PosixFilePermission.OTHERS_READ,
    PosixFilePermission.GROUP_EXECUTE,
    PosixFilePermission.GROUP_WRITE,
    PosixFilePermission.GROUP_READ,
    PosixFilePermission.OWNER_EXECUTE,
    PosixFilePermission.OWNER_WRITE,
    PosixFilePermission.OWNER_READ,
)
