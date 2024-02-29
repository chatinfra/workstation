package ai.chatinfra.ai.chatinfra.server

import ai.chatinfra.bashscript.Bash

interface ConfFile {
     val filename: String
     val content: String

     fun bashDeliverConf(chmod: String? = null): Bash = Bash {
         writeFile(filename, content)
         if (chmod != null) exec("chmod $chmod $filename")
     }

    companion object {

    }
}

 data class ConfFileImpl(override val filename: String, override val content: String) : ConfFile {

}

 fun ConfFile(filename: String, content: String): ConfFile = ConfFileImpl(filename = filename, content = content)

 fun Bash.Builder.writeConf(conf: ConfFile) {
    writeFile(conf.filename, conf.content)
}
