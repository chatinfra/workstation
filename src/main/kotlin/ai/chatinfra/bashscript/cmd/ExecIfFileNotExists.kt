package ai.chatinfra.bashscript.cmd
import ai.chatinfra.ai.chatinfra.bashscript.BashCmd
import ai.chatinfra.bashscript.Bash

class ExecIfFileExists(
    private val absolute: String,
     val innerScript: Bash.Builder.() -> Unit
) : BashCmd {
    override fun render(sudo: Boolean): String {
        return """
        if [ -f "$absolute" ]
        then
            ${Bash(innerScript).render().prependIndent("    ")}
        else
            echo "skipped, file $absolute exists"
        fi

        """.trimIndent()
    }
}

 class ExecIfFileNotExists(
    private val absolute: String,
    private val quiet: Boolean,
     val innerScript: Bash.Builder.() -> Unit
) : BashCmd {
    override fun render(sudo: Boolean): String {
        return """
        if [ ! -f "$absolute" ]
        then
            ${Bash(innerScript).render().prependIndent("    ")}
        else
            echo "${
            if (!quiet) {
                "skipped, file $absolute exists"
            } else {
                ""
            }
        } "
        fi

        """.trimIndent()
    }
}

 class ExecIfDirExists(
    private val absolute: String,
     val innerScript: Bash.Builder.() -> Unit
) : BashCmd {
    override fun render(sudo: Boolean): String {
        return """
        if [ -d "$absolute" ]
        then
            ${Bash(innerScript).render().prependIndent("    ")}
        else
            echo "skipped, dir $absolute does not exists"
        fi
        """.trimIndent()
    }
}
