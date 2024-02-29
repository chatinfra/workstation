package ai.chatinfra.ai.chatinfra.context.systemcall

import ai.chatinfra.ai.chatinfra.context.MinimalContext
import ai.chatinfra.ai.chatinfra.context.exec.ErrorContext
import ai.chatinfra.context.systemcall.SystemCallException
import ai.chatinfra.context.systemcall.maybeSudoPrefix
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


suspend fun MinimalContext.systemCallStream(
    cmd: String,
    timeout: Duration? = 30.seconds,
    tmp: String = "/tmp",
    sudo: Boolean = false,
    logLine: (String) -> Unit = { },
    finished: () -> Boolean = { true },
): Res<SystemCallResult> = SystemCallStream {
    this.cmd = cmd
    this.timeout = timeout
    this.dir = tmp
    this.sudo = sudo
    this.logLine = logLine
    this.mc = this@systemCallStream
    this.finished = finished
}.execute()

private class SystemCallStream private constructor(builder: Builder, val mc: MinimalContext) : MinimalContext by mc {
    val timeout: Duration? = builder.timeout
    val cmd: String = builder.cmd!!
    val dir: String = builder.dir
    val logLine: (String) -> Unit = builder.logLine
    val sudo: Boolean = builder.sudo
    val finished: () -> Boolean = builder.finished

    companion object {
        operator fun invoke(block: Builder.() -> Unit): SystemCallStream = Builder().apply(block).build()
    }

    class Builder {
        var timeout: Duration? = 30.seconds
        var cmd: String? = null
        var dir: String = "/tmp"
        var logLine: (String) -> Unit = { }
        var sudo: Boolean = false
        lateinit var mc: MinimalContext
        lateinit var finished: () -> Boolean

        @PublishedApi
        internal fun build(): SystemCallStream = SystemCallStream(this, mc)
    }

    class LoggingReader(
        private val reader: BufferedReader,
        private val logLine: (String) -> Unit,
        val mc: ErrorContext,
        val finished: () -> Boolean
    ) : ErrorContext by mc {

        suspend fun start(): Unit {
            withIO {
                while (!finished()) {
                    if (reader.ready()) withMain { logLine(reader.readLine()) } else delay(100.milliseconds)
                }
            }

        }
    }


    class StderrLoggingReader(
        private val reader: BufferedReader,
        val mc: ErrorContext,
        val finished: () -> Boolean

    ) : ErrorContext by mc {
        var buf: String = ""


        suspend fun start(): Unit {
            withIO {
                while (!finished()) {
                    if (reader.ready()) buf += reader.readLine() + "\n" else delay(100.milliseconds)
                }
            }
        }
    }

}


private suspend fun SystemCallStream.execute(): Res<SystemCallResult> {
    lateinit var r: Res<SystemCallResult>
    var process: Process? = null
    var outReader: SystemCallStream.LoggingReader? = null
    var errReader: SystemCallStream.StderrLoggingReader? = null


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

            outReader = SystemCallStream.LoggingReader(inputStream, logLine, mc, finished).apply { outJob = io.launchHandling { start() } }
            errReader = SystemCallStream.StderrLoggingReader(errorStream, mc, finished).apply { inJob = io.launchHandling { start() } }

            val start = Clock.System.now().toEpochMilliseconds()
            delay(1.seconds)

            if (timeout != null) {
                while ((Clock.System.now().toEpochMilliseconds() - start < timeout.inWholeMilliseconds && (outJob.isActive || inJob.isActive)) && !finished()) {
                    delay(100.milliseconds)
                }
            } else {
                while (((outJob.isActive || inJob.isActive)) && !finished()) {
                    delay(100.milliseconds)
                }
            }


            if (finished()) {
                inputStream.close()
                errorStream.close()
                process!!.destroyForcibly()
            }

            if (process!!.isAlive) {
                delay(100.milliseconds)
                if (process!!.isAlive) {
                    throw SystemCallException(stdout = "", stderr = "errReader?.buf?.trim()", exitCode = process!!.exitValue(), timeout = true)
                }
            }
            if (process!!.exitValue() != 0) {
                throw SystemCallException(stdout = "", stderr = errReader?.buf?.trim(), exitCode = process!!.exitValue(), timeout = false)
            }
        }


    }.onSuccess {
        r = Ok(SystemCallResult(stdout = "", stderr = errReader?.buf?.trim(), process?.exitValue()))
    }.onFailure {
        delay(100.milliseconds)

        r = if (finished()) {
            Ok(SystemCallResult(stdout = "", stderr = errReader?.buf?.trim(), 0))
        } else when (it) {


            is SystemCallException -> Err(it)
            else -> Err(
                SystemCallException(
                    stdout = "",
                    stderr = errReader?.buf?.trim() + it.toString(),
                    exitCode = if (process?.isAlive == false) process?.exitValue() else null,
                    timeout = process?.isAlive == true
                )
            )
        }
    }
    return r
}


public fun shellSplit(string: CharSequence): List<String> {
    val tokens = ArrayList<String>()
    var escaping = false
    var quoteChar = ' '
    var quoting = false
    var current = StringBuilder()
    for (i in 0 until string.length) {
        val c = string[i]
        if (escaping) {
            current.append(c)
            escaping = false
        } else if (c == '\\' && !(quoting && quoteChar == '\'')) {
            escaping = true
        } else if (quoting && c == quoteChar) {
            quoting = false
        } else if (!quoting && (c == '\'' || c == '"')) {
            quoting = true
            quoteChar = c
        } else if (!quoting && Character.isWhitespace(c)) {
            if (current.isNotEmpty()) {
                tokens.add(current.toString())
                current = StringBuilder()
            }
        } else {
            current.append(c)
        }
    }
    if (current.isNotEmpty()) {
        tokens.add(current.toString())
    }
    return tokens
}
