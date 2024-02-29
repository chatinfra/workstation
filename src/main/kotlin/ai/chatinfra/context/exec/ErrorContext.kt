package ai.chatinfra.ai.chatinfra.context.exec

import ai.chatinfra.ai.chatinfra.context.MinimalContext
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.seconds

class OnCloseEvent()

 interface ErrorContext : CoroutineScope {
    val error: ErrorChannel
    val main: CoroutineContext
    val io: CoroutineContext
    val onError: OnEvent<ErrorChannel.Event>

    fun childEc(name: String): ErrorContext = copy(main = coroutineContext + CoroutineName(name), io = io + CoroutineName(name))

    fun exitErrorContext(message: String) {
        launchHandling { error.close() }
        throw Exception()
    }

    data class ErrorContextCloseResult(val errors: List<ErrorChannel.Event>, val closeErrors: List<Throwable>) {
        fun printErrorContextCloseCloseResult() {
            println("ErrorContextCloseResult")
            println("errors $errors")
            println("closeErrors $closeErrors")
        }
    }

    fun <T> CoroutineContext.launchHandling(message: String? = null, function: suspend ErrorContext.() -> T): Job = with(CoroutineScope(this)) { launchHandling(message, function) }

    fun handleException(e: Throwable) {
        e.printStackTrace()
        CoroutineScope(main).launch { error.trySend(e) }
    }
//    suspend fun <T> CoroutineContext.runHandling(message: String? = null, function: suspend ErrorContext.() -> T): T = withContext(this) {
//        runCatching {
//            function()
//        }.onFailure {
//            it.printStackTrace()
//            error.send(it, message ?: "")
//        }
//    }


    fun <T> CoroutineScope.launchHandling(message: String? = null, function: suspend ErrorContext.() -> T): Job = launch {
        runHandling(message, function)
    }


    suspend fun <T> runHandlingReturning(message: String? = null, function: suspend ErrorContext.() -> T): T {
        var r: T? = null
        runCatching {
            r = function()
        }.onFailure {
            it.printStackTrace()
            error.trySend(it, message ?: "")
        }
        return r!!
    }

    suspend fun <T> runHandling(message: String? = null, function: suspend ErrorContext.() -> T) = runCatching {
        function()
    }.onFailure {
        it.printStackTrace()
        error.trySend(it, message ?: "")
    }


     suspend fun <T> withMain(function: suspend () -> T): T = withContext(main) { function() }
     suspend fun <T> withIO(function: suspend () -> T): T = withContext(io) { function() }

    fun throwOnHasErrors() {
        if (error.hasErrors) throw Exception("error.hasErrors")
    }

    companion object {
        suspend fun create(main: CoroutineContext? = null, io: CoroutineContext = Dispatchers.IO, name: String = "ErrorContext",): ErrorContext {
            val m = main ?: coroutineContext
            return ErrorContextImpl(
                main = m + CoroutineName("$name.main"),
                io = io + CoroutineName("$name.io"),
                error = ErrorChannel(main = m + CoroutineName("$name.ErrorChannel"))
            )
        }
    }
}

fun List<ErrorChannel.Event>.throwOnAnyError() {
    if (isNotEmpty()) throw first().throwable
}

fun ErrorContext.ErrorContextCloseResult.throwOnAnyError() {
    errors.throwOnAnyError()
    closeErrors.throwOnAnyException()
}

 fun List<Throwable>.throwOnAnyException(): List<Throwable> = apply {
    if (isNotEmpty()) throw first()
}


data class ErrorContextImpl(
    override val main: CoroutineContext,
    override val io: CoroutineContext,
    override val error: ErrorChannel,
) : ErrorContext {
    override val onError: OnEvent<ErrorChannel.Event> = OnEvent()

    override fun toString(): String {
        return "ErrorContext(error=$error)"
    }

    override val coroutineContext: CoroutineContext
        get() = main

    init {
        CoroutineScope(main + CoroutineName("ERROR.init ")).launch {
            error.receiveAsFlow { event ->
                launchHandling { onError.invoke(event, this@ErrorContextImpl) }
            }
        }
    }
}

 fun ErrorContext(
    main: CoroutineContext,
    io: CoroutineContext,
    error: ErrorChannel = ErrorChannel(main),
): ErrorContext = ErrorContextImpl(main = main, io = io, error = error)

//fun ErrorContext.spawn(name: String) = CoroutineName(name).run { copy(main = coroutineContext + this, io = io + this) }

 fun ErrorContext.copy(
    error: ErrorChannel? = null,
    main: CoroutineContext? = null,
    io: CoroutineContext? = null,
): ErrorContext = ErrorContextImpl(
    main = main ?: this.main,
    io = io ?: this.io,
    error = error ?: this.error,
)

suspend fun <T, C : ErrorContext> C.useReturning(function: suspend C.() -> T): T {
    val errors = mutableListOf<ErrorChannel.Event>()
    onError.add { errors += it }
    var r: T? = null
    runHandling { r = function() }


    delay(1.seconds)
    if (this is MinimalContext.Closable) close()

    error.close()

    return r!!
}


suspend fun <T, C : ErrorContext> C.use(function: suspend C.() -> T): ErrorContext.ErrorContextCloseResult {
    val errors = mutableListOf<ErrorChannel.Event>()
    onError.add { errors += it }
    runHandling("use") { function() }

    delay(1.seconds)

    if (this is MinimalContext.Closable) close()

    error.close()
    return ErrorContext.ErrorContextCloseResult(errors, emptyList())
}

suspend fun <T, C : ErrorContext> C.useIt(function: suspend (C) -> T): ErrorContext.ErrorContextCloseResult {
    val errors = mutableListOf<ErrorChannel.Event>()
    onError.add { errors += it }
    runHandling("use") { function(this@useIt) }
    delay(1.seconds)
    if (this is MinimalContext.Closable) close()
    error.close()
    return ErrorContext.ErrorContextCloseResult(errors, emptyList())
}
