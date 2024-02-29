package ai.chatinfra.ai.chatinfra.check

import ai.chatinfra.ai.chatinfra.util.Res
import kotlin.random.Random

 interface Check {
     val checkId: String
        get() = "unset"


     suspend fun execute(deps: CheckDeps = EmptyCheckDeps()): Res<CheckResult>
      class EmptyCheckDeps( ) : CheckDeps

     val description: String
        get() = this::class.simpleName.toString()

     companion object {
         fun generateId(): String = "policy${Random.nextBytes(6).toString()}"
    }
}