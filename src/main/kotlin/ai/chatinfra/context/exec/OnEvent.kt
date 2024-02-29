package ai.chatinfra.ai.chatinfra.context.exec

import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.util.concurrent.CopyOnWriteArrayList

class OnEvent<M>() {

    class EventFun<M>(val function: suspend (M) -> Unit, val oneTime: Boolean)

    val funs: CopyOnWriteArrayList<EventFun<M>> = CopyOnWriteArrayList()
    private val sem = Semaphore(1)

    fun add(oneTime: Boolean = false, function: suspend (M) -> Unit) = funs.add(EventFun(function, oneTime))

    fun remove(function: suspend (M) -> Unit) = funs.remove(funs.first { it.function == function })

    fun invoke(event: M, ec: ErrorContext) {
        with(ec) {
            main.launchHandling {
                sem.withPermit {
                    funs.onEach { launchHandling { it.function(event) } }
                    funs.filter { it.oneTime }.onEach { funs.remove(it) }
                }
            }
        }
    }
}