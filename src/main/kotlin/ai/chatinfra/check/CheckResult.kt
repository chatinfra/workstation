package ai.chatinfra.ai.chatinfra.check


import ai.chatinfra.ai.chatinfra.util.Res
import ai.chatinfra.ai.chatinfra.util.throwing
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.onSuccess
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.Instant
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlin.math.min


@Serializable
@Polymorphic
 sealed class CheckResult {

     abstract val ts: Instant
     abstract val extra: CheckExtra
     val isUp: Boolean
        get() = (this is Up)
     val isDown: Boolean
        get() = (this is Down)
     val upDown: String
        get() = this::class.simpleName!!

    @Serializable
     data class Up(override val extra: CheckExtra = CheckExtra(), override val ts: Instant = now()) : CheckResult() {
        override fun toString(): String = if (extra.message.isNotBlank()) "Up($extra)" else "Up"
    }

    @Serializable
     data class Down(override val extra: CheckExtra = CheckExtra(), override val ts: Instant = now()) : CheckResult() {
        override fun toString(): String = if (extra.message.isNotBlank()) "Down($extra)" else "Down"
    }

    override fun toString(): String = "${this::class.simpleName}"

     fun Float.format(digits: Int = 2): String = (this * 100).toInt().run {
        "${this / 100}.${(this % 100).toString().padStart(digits, '0')}"
    }
}

 interface CheckExtra : Extra {
     val message: String
}
 interface Extra
 data class ExtraImpl(val message: String) : Extra

 fun Extra(message: String?): Extra = ExtraImpl(message ?: "")

 interface SuccessExtra : Extra
 interface FailureExtra : Extra

 data class ListExtra<E>(val list: List<E> = emptyList()) : CheckExtra {
    override val message: String = toString()
    override fun toString(): String = list.toString()
}

 fun CheckExtra(stdout: String = "", chipLabel: String = ""): CheckExtra = CheckExtraImpl(stdout = stdout, chipLabel = chipLabel)

@Serializable
 data class CheckExtraImpl(val stdout: String = "", val chipLabel: String = "") : CheckExtra {

    override val message: String = toString()

    override fun toString(): String {
        val showLength = 280
        val show = stdout.run { substring(0, min(showLength, length)) }
        val continued = if (stdout.length > showLength) {
            " ... ${stdout.length - showLength} suppressed chars"
        } else {
            ""
        }
        return if (stdout.trim().isBlank()) {
            ""
        } else {
            "Extra(CheckExtra='$show $continued')"
        }
    }
}

 suspend fun List<Res<CheckResult>>.onAnyNotUp(function: suspend () -> Unit): List<Res<CheckResult>> {
    when {
        (filterIsInstance<Err<CheckResult>>() + filterIsInstance<Ok<CheckResult.Up>>()).isNotEmpty() -> function()
        else -> IGNORE
    }
    return this
}
internal val IGNORE = {}

 suspend fun List<Res<CheckResult>>.onAnyFailures(function: suspend (List<Err<Exception>>) -> Unit): List<Res<CheckResult>> {
    when {
        filterIsInstance<Err<Exception>>().isNotEmpty() -> function(filterIsInstance<Err<Exception>>())
        else -> IGNORE
    }
    return this
}
//
// suspend fun List<CheckResult>.onAnyCritical(function: suspend (List<CheckResult.Critical>) -> Unit): List<CheckResult> {
//    when {
//        filterIsInstance<CheckResult.Critical>().isNotEmpty() -> function(filterIsInstance<CheckResult.Critical>())
//        else -> IGNORE
//    }
//    return this
//}

 suspend fun List<CheckResult>.onFullSuccessUp(function: suspend (List<CheckResult.Up>) -> Unit): List<CheckResult> {
    when {
        filterIsInstance<CheckResult.Up>().isNotEmpty() || isEmpty() -> function(filterIsInstance<CheckResult.Up>())
        else -> IGNORE
    }
    return this
}

fun CheckResult.onUp(function: (CheckResult.Up) -> Unit) {
    when (this) {
        is CheckResult.Down -> {}
        is CheckResult.Up -> function(this)
    }

}

fun Res<CheckResult>.onUp(function: (CheckResult.Up) -> Unit) {
    onSuccess {
        when (it) {
            is CheckResult.Down -> {}
            is CheckResult.Up -> function(it)
        }
    }
}

suspend inline fun CheckResult.onDown(function: (CheckResult.Down) -> Unit) {
    when (this) {
        is CheckResult.Down -> function(this)
        is CheckResult.Up -> {}
    }
}

suspend inline fun Res<CheckResult>.onDown(function: (CheckResult.Down) -> Unit) {
    onSuccess {
        when (it) {
            is CheckResult.Down -> function(it)
            is CheckResult.Up -> {}
        }
    }
}


 fun Res<CheckResult>.throwOnNotUp(): Res<CheckResult> = apply {
    if (throwing() !is CheckResult.Up) throw Exception(toString())
}

 fun CheckResult.throwOnNotUp(): CheckResult = apply {
    when (this) {
        is CheckResult.Up -> IGNORE
        else -> throw Exception(toString())
    }
}

 suspend fun List<Res<CheckResult>>.throwOnAnyNotUp() {
    onAnyFailures { throw it.first().error }
    filterIsInstance<CheckResult.Up>().filterNot { it.isUp }.apply {
        if (isNotEmpty()) {
            throw Exception("throwOnAnyNotUp CheckState ${first()}")
        }
    }
}

//  val PendingCheckResult: CheckResult
//    get() = CheckResult.Success.Pending(check = FakeCheck())



