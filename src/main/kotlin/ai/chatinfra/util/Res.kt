package ai.chatinfra.ai.chatinfra.util

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok

 typealias Res<T> = com.github.michaelbull.result.Result<T, Throwable>

 val Res<*>.isOk: Boolean
    get() = this is Ok<*>

 val Res<*>.isErr: Boolean
    get() = this is Err<*>

 fun <T> Res<T>.throwing(): T = when (this) {
    is Ok -> value
    is Err -> throw this.error
}

 fun <T> Res<T>.getOrNull(): T? = when (this) {
    is Ok -> value
    is Err -> null
}


private val IGNORE = {}

 fun <E> List<Res<E>>.onFullSuccess(function: (List<Res<E>>) -> Unit): List<Res<E>> =
    apply {
        when {
            filterIsInstance<Err<E>>().isEmpty() -> function(this)
            else -> IGNORE
        }
    }

 fun <E> List<Res<E>>.onAnyFailure(function: (List<Res<E>>) -> Unit): List<Res<E>> =
    apply {
        when {
            filterIsInstance<Err<E>>().isNotEmpty() -> function(this)
        }
    }
 fun <E> List<Res<E>>.onEachSuccess(function: (Ok<E>) -> Unit): List<Res<E>> =
    apply {
        when {
            filterIsInstance<Ok<E>>().isNotEmpty() ->
                filterIsInstance<Ok<E>>().onEach { function(it) }

        }
    }
 fun <E> List<Res<E>>.onEachFailure(function: (Err<E>) -> Unit): List<Res<E>> =
    apply {
        when {
            filterIsInstance<Err<E>>().isNotEmpty() ->
                filterIsInstance<Err<E>>().onEach { function(it) }

        }
    }

@JvmName("throwOnAnyFailurePair")

 fun List<Pair<String, Res<*>>>.throwOnAnyFailure(): List<Ok<*>> = map { it.second }.throwOnAnyFailure()

 fun <T> List<Res<T>>.throwOnAnyFailure(): List<Ok<T>> {
    val oks = filterIsInstance<Ok<T>>()
    val errs = filterIsInstance<Err<T>>()
    return if (errs.isNotEmpty()) {
        throw errs.first().error as Throwable
    } else {
        oks
    }
}
