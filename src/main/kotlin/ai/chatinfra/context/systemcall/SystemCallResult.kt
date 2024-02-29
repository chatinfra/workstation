package ai.chatinfra.ai.chatinfra.context.systemcall

import kotlinx.serialization.Serializable

@Serializable
data class SystemCallResult(
    val stdout: String,
    val stderr: String?,
    val exitCode: Int?,
) {
    val lines: List<String>
        get() = stdout.trim().lines().filterNot { it.isBlank() }

}