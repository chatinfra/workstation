package ai.chatinfra.context.systemcall

import kotlinx.serialization.Serializable

@Serializable
data class SystemCallException(
    val stdout: String,
    val stderr: String?,
    val exitCode: Int?,
    val timeout: Boolean
) : Exception()

internal fun maybeSudoPrefix(sudo: Boolean): String = when (sudo) {
    true -> "/usr/bin/sudo "
    false -> ""
}

