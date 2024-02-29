package ai.chatinfra.bashscript.cmd

import ai.chatinfra.ai.chatinfra.bashscript.BashCmd

 class SedReplace( val filename: String,  val varname: String,  val newvalue: String) : BashCmd {
    override fun render(sudo: Boolean): String {
        return "sed -i '/^$varname/s/^$newvalue//g' $filename\n"
    }
}
