package ai.chatinfra.ai.chatinfra.context

import affair.lib.util.time.EASTERN

import ai.chatinfra.ai.chatinfra.context.exec.ErrorChannel
import ai.chatinfra.ai.chatinfra.context.exec.ErrorContext
import ai.chatinfra.ai.chatinfra.context.exec.ErrorContextImpl
import ai.chatinfra.ai.chatinfra.context.exec.throwOnAnyError
import ai.chatinfra.ai.chatinfra.context.exec.use
import ai.chatinfra.ai.chatinfra.server.Localhost
import ai.chatinfra.ai.chatinfra.context.systemcall.systemCallStream
import ai.chatinfra.ai.chatinfra.context.time.TimeContext
import ai.chatinfra.context.ConsoleLogContext
import ai.chatinfra.context.LogContext
import ai.chatinfra.context.systemcall.SystemCallContext
import ai.chatinfra.context.systemcall.SystemCallContextImpl
import ai.chatinfra.context.systemcall.systemCallBuf
import ai.chatinfra.ai.chatinfra.util.HOME
import ai.chatinfra.ai.chatinfra.util.Prefix
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.io.path.absolute
import kotlin.io.path.writeText

interface MinimalContext : TimeContext, ErrorContext, LogContext, CoroutineScope {
    val home: String
    val tc: TimeContext
    val ec: ErrorContext
    val lc: LogContext
    val serializers: SerializersModule

    fun copy(
        tc: TimeContext? = null,
        ec: ErrorContext? = null,
        lc: LogContext? = null,
        home: String? = null,
        serializers: SerializersModule? = null
    ): MinimalContext

    override fun setDebug(debug: Boolean): MinimalContext = copy(lc = lc.setDebug(debug))


    val json: Json
        get() = Json {
            serializersModule = serializers
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
    val jsonPretty: Json
        get() = Json {
            serializersModule = serializers
            encodeDefaults = true
            ignoreUnknownKeys = true
            prettyPrint = true
        }

    interface Closable {
        fun close()
    }


    fun JsonElement.prettyPrint(): String = jsonPretty.encodeToString(this)

    override val coroutineContext: CoroutineContext
        get() = ec.coroutineContext

    fun childMc(name: String, debug: Boolean? = null): MinimalContext = MinimalContext(tc, ec.childEc(name), lc.childLc(name, debug), home, serializers)

    companion object {
        fun fromEc(
            ec: ErrorContext,
            tc: TimeContext = TimeContext.createRealtime(),
            debug: Boolean = false,
            home: String = HOME,
            prefix: Prefix? = null,
            lc: LogContext = ConsoleLogContext(debug = debug, prefix = prefix ?: Prefix("log")),
            serializers: SerializersModule = SerializersModule { }
        ): MinimalContextImpl = MinimalContextImpl(tc, ec, lc, home = home, serializers = serializers)

        suspend fun create(
            main: CoroutineContext? = null,
            io: CoroutineContext = Dispatchers.IO,
            debug: Boolean = false,
            prefix: Prefix? = null,
            lc: LogContext = ConsoleLogContext(debug = debug, prefix = prefix ?: Prefix("log")),
            tc: TimeContext = TimeContext.createRealtime(TimeZone.EASTERN),
            home: String = HOME,
            serializers: SerializersModule = SerializersModule { }
        ): MinimalContext {
            val m = main ?: coroutineContext
            CoroutineName("mc")

            return MinimalContext(
                tc = tc,
                ec = ErrorContextImpl(
                    main = m + CoroutineName("mc.main"),
                    io = (io) + CoroutineName("mc.io"),
                    ErrorChannel(main = m + CoroutineName("mc.error"))
                ),
                lc = lc,
                home = home,
                serializers = serializers,
            )
        }
    }
}


inline fun runBlockingSystemCall(debug: Boolean, crossinline function: suspend SystemCallContext.() -> Unit) = runBlocking {
    MinimalContext.create(debug = debug).use {
        coroutineScope {
            runHandling {
                function(SystemCallContextImpl(
                    mc = this@use,
                    localhost = Localhost,
                    execBash = {
                        val f = kotlin.io.path.createTempFile("bash").logIt()
                        f.writeText(it.render())
                        systemCallStream("chmod 700 $it && bash -x -e ${f.absolute()}", logLine = { println(it) }).logIt()
                    },
                    execCmd = { systemCallBuf(it).logIt() }
                ))
            }
        }
    }.throwOnAnyError()
}


inline fun runBlockingMinimal(debug: Boolean, crossinline function: suspend MinimalContext.() -> Unit) = runBlocking {
    MinimalContext.create(debug = debug).use {
        coroutineScope {
            launchHandling { function() }
        }
    }.throwOnAnyError()
}


inline fun <reified T> MinimalContext.prettyPrint(json: Json = jsonPretty): String = json.encodeToString(this)

@OptIn(InternalSerializationApi::class)
inline fun <reified T : Any> MinimalContext.prettyPrint(o: T): String = jsonPretty.encodeToString(T::class.serializer(), o)

fun MinimalContext.registerShutdownHook(function: () -> Unit) {
    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run(): Unit = runBlocking {
            function()
        }
    })
}

fun MinimalContext(
    tc: TimeContext,
    ec: ErrorContext,
    lc: LogContext = ConsoleLogContext(Prefix("mc")),
    home: String = HOME,
    serializers: SerializersModule = SerializersModule { }
): MinimalContext = MinimalContextImpl(tc, ec, lc, home, serializers)

data class MinimalContextImpl(
    override val tc: TimeContext,
    override val ec: ErrorContext,
    override val lc: LogContext,
    override val home: String = HOME,
    override val serializers: SerializersModule,
) : MinimalContext, ErrorContext by ec, TimeContext by tc, LogContext by lc {
    override fun setDebug(debug: Boolean): MinimalContext = copy(lc = lc.setDebug())
    override val coroutineContext: CoroutineContext
        get() = ec.coroutineContext

    override fun toString(): String {
        return "MinimalContextImpl(tc=$tc, ec=$ec, lc=$lc, home='$home')"
    }

    override fun copy(
        tc: TimeContext?,
        ec: ErrorContext?,
        lc: LogContext?,
        home: String?,
        serializers: SerializersModule?
    ): MinimalContext = MinimalContextImpl(
        tc = tc ?: this.tc,
        ec = ec ?: this.ec,
        lc = lc ?: this.lc,
        home = home ?: this.home,
        serializers = serializers ?: this.serializers,
    )

}
