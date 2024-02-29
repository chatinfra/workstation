package ai.chatinfra.context

import ai.chatinfra.ai.chatinfra.util.Prefix


interface LogContext {
    val prefix: Prefix
    val debug: Boolean
    val sanitizeReplace: Map<String, String>

    fun childLc(name: String, debug: Boolean?): LogContext
    fun setDebug(debug: Boolean = true): LogContext

    fun log(l: Any): Unit {
        if (debug) println(format(l))
    }

    fun format(l: Any): String = "${prefix.dotted}.$l".run {
        var r = this
        sanitizeReplace.onEach { r = r.replace(it.key, it.value) }
        r
    }

    fun <T> T.logIt(s: String? = null): T = apply { log(((s ?: "") + " -> ") + toString()) }

    fun <E> Set<E>.logSet(prefix: String? = null): Set<E> = apply {
        forEach { log("${prefix?.let { "$it -> " } ?: ""}$it") }
        if (isEmpty()) {
            log("$prefix ${this::class.simpleName} -> EMPTY Set")
        }
    }

    fun <K, V> Map<K, V>.logMap(prefix: String? = null): Map<K, V> = apply {
        forEach { log("${prefix?.let { "$it -> " } ?: ""}$it") }
        if (isEmpty()) {
            log("$prefix ${this::class.simpleName} -> EMPTY Set")
        }
    }
//    fun <E> MutableSet<E>.logSet(prefix: String? = null): MutableSet<E> = apply {
//        forEach { log("${prefix?.let { "$it -> " } ?: ""}$it") }
//        if (isEmpty()) { log("$prefix ${this::class.simpleName} -> EMPTY MutableSet") }
//    }

    fun <E> List<E>.logList(prefix: String? = null): List<E> = apply {
        forEach { log("${prefix?.let { "$it -> " } ?: ""}$it") }
        if (isEmpty()) {
            log("$prefix ${this::class.simpleName} -> EMPTY List")
        }
    }
//    fun <E> MutableList<E>.logList(prefix: String? = null): MutableList<E> = apply {
//        forEach { log("${prefix?.let { "$it -> " } ?: ""}$it") }
//        if (isEmpty()) { log("$prefix ${this::class.simpleName} -> EMPTY MutableList") }
//    }

    companion object {
        fun create(name: String, debug: Boolean = false): LogContext = ConsoleLogContext(prefix = Prefix(name), debug = debug)
        fun createExceptionSanitizing(s: String): LogContext = ConsoleLogContext(
            prefix = Prefix(s), sanitizeReplace = mapOf(
                "Exception" to "Excheption",
                "Error" to "Erhhor"
            )
        )
    }
}


data class ConsoleLogContext(
    override val prefix: Prefix = Prefix("log"),
    override val debug: Boolean = true,
    override val sanitizeReplace: Map<String, String> = emptyMap(),
) : LogContext {
    override fun childLc(name: String, debug: Boolean?): LogContext = ConsoleLogContext(prefix = prefix + name, debug = debug ?: this.debug)
    override fun setDebug(debug: Boolean): LogContext = copy(debug = debug)
}
