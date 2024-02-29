package ai.chatinfra.bashscript

import ai.chatinfra.ai.chatinfra.bashscript.BashCmd
import ai.chatinfra.ai.chatinfra.bashscript.cmd.AddLine
import ai.chatinfra.ai.chatinfra.bashscript.cmd.AptAutoRemove
import ai.chatinfra.ai.chatinfra.bashscript.cmd.ExecIfDirNotExists
import ai.chatinfra.bashscript.cmd.AptDistUpgrade
import ai.chatinfra.bashscript.cmd.AptInstall
import ai.chatinfra.bashscript.cmd.AptUpdate
import ai.chatinfra.bashscript.cmd.Chown
import ai.chatinfra.bashscript.cmd.DpkgInstall
import ai.chatinfra.bashscript.cmd.Echo
import ai.chatinfra.bashscript.cmd.ExecIfDirExists
import ai.chatinfra.bashscript.cmd.ExecIfFileExists
import ai.chatinfra.bashscript.cmd.ExecIfFileNotExists
import ai.chatinfra.bashscript.cmd.ExecuteCommand
import ai.chatinfra.bashscript.cmd.MkDir
import ai.chatinfra.bashscript.cmd.SedReplace
import ai.chatinfra.bashscript.cmd.Service
import ai.chatinfra.bashscript.cmd.SetupAwsClient
import ai.chatinfra.bashscript.cmd.UfwAllow
import ai.chatinfra.bashscript.cmd.WriteFile
import kotlinx.serialization.Serializable

@DslMarker
 annotation class BashScriptTagMarker

@Serializable
@BashScriptTagMarker
 class Bash private constructor(private val builder: Builder) {
    private val fragments: List<BashCmd> = builder.fragments
     var debug: Boolean = builder.debug

     fun render(sudo: Boolean = false, addHashBang: Boolean = false): String = if (addHashBang) {
        "#!/bin/bash\n\n"
    } else {
        ""
    } + fragments.map { it.render(sudo) }.joinToString(separator = "\n") { it }

     operator fun plus(b2: Bash?): Bash = if (b2 != null) Builder(fragments + b2.fragments).build() else this


     companion object {
         operator fun invoke(block: Builder.() -> Unit): Bash = Builder().apply(block).build()
    }

    @Serializable
     class Builder {
         val fragments: MutableList<BashCmd> = mutableListOf()
         var debug: Boolean = false



         constructor()
         constructor(fragments: List<BashCmd>) {
            this.fragments += fragments
        }

        @PublishedApi
        internal fun build(): Bash = Bash(this)

        @BashScriptTagMarker
         fun echo(message: String) {
            fragments += Echo(message)
        }

         fun BashScript(script: Builder.() -> Unit) {
            fragments += invoke(script).fragments
        }

         fun writeFile(
            filename: String,
            frag: String,
            append: Boolean = false,
            makeExecutable: Boolean = false
        ) {
            fragments += WriteFile(filename, frag + "\n", append, makeExecutable)
        }

         fun exec(cmd: BashCmd) {
            fragments += ExecuteCommand(cmd.render())
        }

         fun exec(command: String) {
            fragments += ExecuteCommand(command)
        }

         fun mkdir(dir: String) {
            fragments += MkDir(dir)
        }

//         fun ifDirNotExists(dir: String, function: Builder.() -> Unit) {
//            fragments += ExecIfDirNotExists(dir, function)
//        }

         fun execIfDirExists(dir: String, function: Builder.() -> Unit) {
            fragments += ExecIfDirExists(dir, function)
        }

         fun execIfDirNotExists(dir: String, function: Builder.() -> Unit) {
            fragments += ExecIfDirNotExists(dir, function)
        }

         fun execIfFileExists(absolute: String, function: Builder.() -> Unit) {
            fragments += ExecIfFileExists(absolute, function)
        }

         fun execIfFileNotExists(absolute: String, quiet: Boolean = false, function: Builder.() -> Unit) {
            fragments += ExecIfFileNotExists(absolute, quiet, function)
        }

         fun chown(dir: String, userGroup: String, recursive: Boolean = false) {
            fragments += Chown(dir = dir, userGroup = userGroup, recursive = recursive)
        }

         fun chmod(mode: String, path: String, recursive: Boolean = false, force: Boolean = false) {
            fragments += ExecuteCommand("chmod $mode ${if (force) "-f " else ""} ${if (recursive) "-R " else ""} $path ${if (force) "|| true " else ""}")
        }

         fun aptInstall(vararg pkg: String, options: String = "", reinstall: Boolean = false) {
            fragments += AptInstall(pkg, options = options, reinstall = reinstall)
        }

         fun aptInstall(pkg: List<String>, options: String = "", reinstall: Boolean = false) {
            fragments += AptInstall(pkg.toTypedArray(), options = options, reinstall = reinstall)
        }

         fun aptAutoRemove() {
            fragments += AptAutoRemove()
        }

         fun aptDistUpgrade() {
            fragments += AptDistUpgrade()
        }

         fun aptUpdate() {
            fragments += AptUpdate()
        }

         fun ufwAllow(vararg port: Int) {
            fragments += UfwAllow(port.toList())
        }

         fun ufwAllow(ports: List<Int>) {
            fragments += UfwAllow(ports)
        }

         fun setupAwsClient(config: String, credentials: String) {
            fragments += SetupAwsClient(config = config, credentials = credentials)
        }

         fun addLine(line: String, filename: String) {
            fragments += AddLine(line, filename)
        }

         fun service(serviceName: String, action: Action) {
            fragments += Service(serviceName = serviceName, action = action.cmd,)
        }

         fun serviceStart(serviceName: String) {
            fragments += Service(serviceName = serviceName, action = Action.Start.cmd,)
        }

         fun serviceStop(serviceName: String, ignoreError: Boolean = false) {
            fragments += Service(serviceName = serviceName, action = Action.Stop.cmd, ignoreError = ignoreError)
        }

         fun serviceRestart(serviceName: String) {
            fragments += Service(serviceName = serviceName, action = Action.Restart.cmd,)
        }

         fun serviceEnable(serviceName: String) {
            fragments += Service(serviceName = serviceName, action = Action.Enable.cmd,)
        }

         fun serviceDisable(serviceName: String) {
            fragments += Service(serviceName = serviceName, action = Action.Disable.cmd,)
        }

         fun daemonReload() {
            fragments += ExecuteCommand("systemctl daemon-reload")
        }

         fun sedSetVariable(filename: String, varname: String, newvalue: String) {
            fragments += SedReplace(filename = filename, varname = varname, newvalue = newvalue)
        }

         fun dpkgInstall(filename: String) {
            fragments += DpkgInstall(filename)
        }

         fun addUser(user: String, home: String, ignoreError: Boolean) {
            fragments += ExecuteCommand("useradd -m -d $home $user" + if (ignoreError) " || true" else "")
        }


    }

     sealed class Action {
         abstract val cmd: String

         object Restart : Action() {
            override val cmd: String = "restart"
        }

         object Stop : Action() {
            override val cmd: String = "stop"
        }

         object Start : Action() {
            override val cmd: String = "start"
        }

         object Enable : Action() {
            override val cmd: String = "enable"
        }

         object Disable : Action() {
            override val cmd: String = "disable"
        }
    }
}

 fun List<Bash>.combine(): Bash {
    var r = Bash {}
    forEach {
        r += it
    }
    return r
}

 fun Bash.Builder.exportEnv(entries: Map<String, String>) {
    entries.onEach { exec("export ${it.key}=${it.value}") }
}