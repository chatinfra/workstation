package ai.chatinfra.ai.chatinfra.server

 sealed class RamSize {
     object OneGb : RamSize() {
         val mb: Int = 1024
         val gb: Int = 1
    }

     object TwoGb : RamSize() {
         val mb: Int = 2048
         val gb: Int = 2
    }

     object FourGb : RamSize() {
         val mb: Int = 4096
         val gb: Int = 4
    }

     object EightGb : RamSize() {
         val mb: Int = 8192
         val gb: Int = 8
    }

     object SixteenGB : RamSize() {
         val mb: Int = 16384
         val gb: Int = 16
    }

     object ThirtyTwoGB : RamSize() {
         val mb: Int = 32768
         val gb: Int = 32
    }

     object SixtyFourGB : RamSize() {
         val mb: Int = 65536
         val gb: Int = 64
    }
}