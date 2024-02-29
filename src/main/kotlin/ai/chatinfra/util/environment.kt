package ai.chatinfra.ai.chatinfra.util

val HOME: String
    get() = System.getenv()["HOME"] ?: throw Exception("HOME not found in System.getenv")

 val DATATMP: String
    get() = "/data/tmp"

 val SSH_HOME: String
    get() = "$HOME/.ssh"


 val USER: String
    get() = System.getenv()["USER"] ?: throw Exception("USER not found in System.getenv")