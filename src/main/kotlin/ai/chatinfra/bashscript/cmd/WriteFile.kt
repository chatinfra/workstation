package ai.chatinfra.bashscript.cmd

import ai.chatinfra.ai.chatinfra.bashscript.BashCmd
import affair.lib.bashscript.configMkdirSkip

 data class WriteFile(
    val filename: String,
    val content: String,
    val append: Boolean = false,
    val makeExecutable: Boolean = false,
    val mkdirSkip: List<String> = configMkdirSkip + ""
) : BashCmd {
    override fun render(sudo: Boolean): String {
        val mode = if (append) ">>" else ">"
        val maybeMkdir = when {
            filename.count { it == '/' } > 0 -> {
                val dir = filename.substringBeforeLast("/")
                if (dir !in mkdirSkip) {
                    "mkdir -p $dir"
                } else {
                    ""
                }
            }
            else -> ""
        }
        var result = """
$maybeMkdir            
cat $mode $filename << EOF
${content.replace("\$", "\\\$")}
EOF

        """.trimIndent()
        if (makeExecutable) {
            result += "chmod o+x $filename\n"
        }
        return result
    }
}
