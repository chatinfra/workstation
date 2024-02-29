package ai.chatinfra.ai.chatinfra.bashscript.cmd

import ai.chatinfra.ai.chatinfra.bashscript.BashCmd
import ai.chatinfra.bashscript.Bash

class ExecIfDirNotExists(
    private val dir: String,
     val innerScript: Bash.Builder.() -> Unit
) : BashCmd {
    override fun render(sudo: Boolean): String {
        return """
        if [ ! -d "$dir" ]
        then
            ${Bash(innerScript).render().prependIndent("    ")}
        else
            echo "skipped, directory $dir exists"
        fi

        """.trimIndent()
    }
}
