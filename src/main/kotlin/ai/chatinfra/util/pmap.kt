package ai.chatinfra.ai.chatinfra.util

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

 suspend fun <A, B> Iterable<A>.pmapIndexed(enableAsync: Boolean = true, f: suspend (Int, A) -> B): List<B> = if (enableAsync) {
    coroutineScope {
        mapIndexed { i, it -> async { f(i, it) } }.awaitAll()
    }
} else {
    mapIndexed { i, it -> f(i, it) }
}

 suspend fun <A, B> Iterable<A>.pmap(enableAsync: Boolean = true, f: suspend (A) -> B): List<B> = if (enableAsync) {
    coroutineScope {
        map { async { f(it) } }.awaitAll()
    }
} else {
    map { f(it) }
}
