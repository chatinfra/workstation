package ai.chatinfra.ai.chatinfra.server

import ai.chatinfra.ai.chatinfra.check.Check
import ai.chatinfra.ai.chatinfra.net.Endpoint
import ai.chatinfra.bashscript.Bash
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Polymorphic
 interface App {

     interface HasChecks : App {
         fun checks(deps: ServerDeps): List<Check>
    }

    interface HasChildren : App {
        val children: List<App>
    }

     interface HasEndpoints {
         val endpoints: List<Endpoint>

         interface Single : HasEndpoints {
             val endpoint: Endpoint
             override val endpoints: List<Endpoint>
                get() = listOf(endpoint)
        }

         interface Multiple : HasEndpoints {
             override val endpoints: List<Endpoint>
        }
    }

     interface HasGithubRepo : App {
         val gitRepo: GithubRepo
    }
     interface CoreGitRepo {
         val url: String
    }
     interface HasLogs : App {
         val logs: List<LogFile>
    }

     interface HasReference : App {
         val reverenceUrls: List<String>
    }

     interface HasService : App {
         val serviceName: String

         interface WithDependants : HasService {
             val dependentServices: List<String>
        }
    }

     interface HasSyslog : App {
         val serviceName: String

         val syslog: SyslogFile
            get() = when (serviceName) {
                "rsyslog" -> SyslogFile(null)
                else -> SyslogFile(serviceName)
            }
    }

     interface HasUser : App {
         val user: String
         val group: String
         val home: String
         val userGroup: String
            get() = "$user:$group"
    }

     interface HasInstall : App {
        suspend fun bashInstall(deps: ServerDeps): Bash
    }

     interface HasReconfigure : App {
         suspend fun bashReconfigure(deps: ServerDeps): Bash
    }

}
 interface CoreGitRepo {
     val url: String
}

@Serializable
 data class GithubRepo(val owner: String, val user: String) : CoreGitRepo {
    override val url: String
        get() = "https://github.com/$owner/$user"
}
