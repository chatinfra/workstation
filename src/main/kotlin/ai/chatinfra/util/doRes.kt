package ai.chatinfra.ai.chatinfra.util

import com.github.michaelbull.result.runCatching

 suspend inline fun <T> doRes(
    crossinline function: suspend () -> T,
): Res<T> = runCatching { function() }

 inline fun <T> doResNoSuspend(
    crossinline function: () -> T,
): Res<T> = runCatching { function() }


