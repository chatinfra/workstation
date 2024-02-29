package ai.chatinfra.context.systemcall

import ai.chatinfra.ai.chatinfra.context.MinimalContext
import ai.chatinfra.ai.chatinfra.context.exec.ErrorContext
import ai.chatinfra.ai.chatinfra.context.systemcall.SystemCallResult
import ai.chatinfra.ai.chatinfra.context.systemcall.shellSplit
import ai.chatinfra.ai.chatinfra.util.Res
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import java.io.BufferedReader
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

public suspend fun MinimalContext.systemCallBuf(
    cmd: String,
    timeout: Duration? = 30.seconds,
    tmp: String = "/tmp",
    sudo: Boolean = false,
): Res<SystemCallResult> = SystemCallBuf {
    this.cmd = cmd
    this.timeout = timeout
    this.dir = tmp
    this.sudo = sudo
    this.mc = this@systemCallBuf
}.execute()

private class SystemCallBuf private constructor(builder: Builder, val mc: MinimalContext) : MinimalContext by mc {
    public val timeout: Duration? = builder.timeout
    public val cmd: String = builder.cmd!!
    public val dir: String = builder.dir
    public val sudo: Boolean = builder.sudo

    public companion object {
        public operator fun invoke(block: Builder.() -> Unit): SystemCallBuf = Builder().apply(block).build()
    }

    public class Builder {
        public var timeout: Duration? = 30.seconds
        public var cmd: String? = null
        public var dir: String = "/tmp"
        public var sudo: Boolean = false
        lateinit var mc: MinimalContext

        @PublishedApi
        internal fun build(): SystemCallBuf = SystemCallBuf(this, mc)
    }

    class LoggingReader(
        private val reader: BufferedReader,
        val mc: ErrorContext,
        val process: Process,
        var buf: String = ""
    ) : ErrorContext by mc {
        val cleanBuf: String
            get() = buf.trim().lines().filterNot { it.isBlank() }.joinToString("\n")

        public suspend fun start(): Unit {
            withIO {
                while (process.isAlive || reader.ready()) {
                    if (reader.ready()) withMain { buf += "${reader.readLine()}\n" } else delay(100.milliseconds)
                }
            }
        }
    }

    class StderrLoggingReader(
        private val reader: BufferedReader,
        val mc: ErrorContext,
        val process: Process,

        ) : ErrorContext by mc {
        public var buf: String = ""


        public suspend fun start(): Unit {
            withIO {
                while (process.isAlive || reader.ready()) {
                    if (reader.ready()) withMain { buf += reader.readLine() + "\n" } else delay(100.milliseconds)
                }
            }
        }
    }

}


private suspend fun SystemCallBuf.execute(): Res<SystemCallResult> {
    lateinit var r: Res<SystemCallResult>
    var process: Process? = null
    var outReader: SystemCallBuf.LoggingReader? = null
    var errReader: SystemCallBuf.StderrLoggingReader? = null


    kotlin.runCatching {
        val cmds: List<String> = shellSplit(maybeSudoPrefix(sudo) + cmd)
        withIO {
            val builder = ProcessBuilder(cmds).directory(
                File(dir).apply { mkdirs() }
            ).apply {
                redirectOutput(ProcessBuilder.Redirect.PIPE)
                redirectError(ProcessBuilder.Redirect.PIPE)
            }
            process = builder.start()
            lateinit var outJob: Job
            lateinit var inJob: Job


            val inputStream: BufferedReader = process!!.inputStream.bufferedReader()
            val errorStream: BufferedReader = process!!.errorStream.bufferedReader()

            outReader = SystemCallBuf.LoggingReader(inputStream, mc, process!!).apply { outJob = main.launchHandling { start() } }
            errReader = SystemCallBuf.StderrLoggingReader(errorStream, mc, process!!).apply { inJob = io.launchHandling { start() } }

            val start = Clock.System.now().toEpochMilliseconds()
            delay(1.seconds)

            if (timeout != null) {
                while ((Clock.System.now().toEpochMilliseconds() - start < timeout.inWholeMilliseconds && (outJob.isActive || inJob.isActive))) {
                    delay(100.milliseconds)
                }
            } else {
                while (process!!.isAlive) {
                    println("process.isAlive ${process?.isAlive}")
                    delay(100.milliseconds)
                }
            }
            if (process!!.isAlive) {
                delay(100.milliseconds)
                if (process!!.isAlive) {
                    throw SystemCallException(stdout = outReader!!.cleanBuf, stderr = "errReader?.buf?.trim()", exitCode = process!!.exitValue(), timeout = true)
                }
            }
            if (process!!.exitValue() != 0) {
                 throw SystemCallException(stdout = outReader!!.cleanBuf, stderr = errReader?.buf?.trim(), exitCode = process!!.exitValue(), timeout = false)
            }
        }
    }.onSuccess {
        r = Ok(SystemCallResult(stdout = outReader!!.cleanBuf, stderr = errReader?.buf?.trim(), process?.exitValue()))
    }.onFailure {
        delay(100.milliseconds)

        r = when (it) {
            is SystemCallException -> Err(it)
            else -> Err(
                SystemCallException(
                    stdout = outReader!!.cleanBuf,
                    stderr = errReader?.buf?.trim() + it.toString(),
                    exitCode = if (process?.isAlive == false) process?.exitValue() else null,
                    timeout = process?.isAlive == true
                )
//                    .also { println("THISIS.ststemcall.onFailure.inst $it") }
            )
        }
    }
    return r
}
