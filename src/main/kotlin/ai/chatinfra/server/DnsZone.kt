package ai.chatinfra.ai.chatinfra.server

import kotlinx.serialization.Serializable

const val DEFAULT_RECORD_TTL: Long = 3600

@Serializable
sealed class Zone {
    abstract val id: String?
    abstract val name: String
    abstract val records: List<Record>

    @Serializable
    data class Forward(override val name: String, override val records: List<Record>, override val id: String? = null) : Zone()

    @Serializable
    data class Reverse(override val name: String, override val records: List<Record>, override val id: String? = null) : Zone()

    @Serializable
    sealed class Record {
        abstract val name: String
        abstract val typeName: String
        abstract val value: String
        abstract val ttl: Long

        @Serializable
        data class Cname(override val name: String, val values: List<String>, override val ttl: Long = DEFAULT_RECORD_TTL) : Record() {
            override val typeName: String
                get() = "CNAME"
            override val value: String
                get() = values.joinToString()
        }

        @Serializable
        data class Mx(override val name: String, val values: List<String>, override val ttl: Long = DEFAULT_RECORD_TTL) : Record() {
            override val typeName: String
                get() = "MX"
            override val value: String
                get() = values.joinToString("\n")
        }

        @Serializable
        data class A(override val name: String, val values: List<String>, override val ttl: Long = DEFAULT_RECORD_TTL) : Record() {
            override val typeName: String
                get() = "A"
            override val value: String
                get() = values.joinToString()
        }

        @Serializable
        data class Ns(override val name: String, val values: List<String>, override val ttl: Long = DEFAULT_RECORD_TTL) : Record() {
            override val typeName: String
                get() = "NS"
            override val value: String
                get() = values.joinToString()
        }

        @Serializable
        data class Soa(override val name: String, val data: String, override val ttl: Long = DEFAULT_RECORD_TTL) : Record() {
            override val typeName: String
                get() = "SOA"
            override val value: String
                get() = data
        }

        @Serializable
        data class Txt(
            override val name: String,
            val data: String,
            override val ttl: Long = DEFAULT_RECORD_TTL
        ) : Record() {
            override val typeName: String
                get() = "TXT"
            override val value: String
                get() = data
        }

        @Serializable
        data class Spf(
            override val name: String,
            val data: String,
            override val ttl: Long = DEFAULT_RECORD_TTL
        ) : Record() {
            override val typeName: String
                get() = "SPF"
            override val value: String
                get() = data
        }

        @Serializable
        data class Srv(
            override val name: String,
            val priority: Int = 10,
            val weight: Int = 10,
            val target: Hostname,
            val port: Int,
            override val ttl: Long = DEFAULT_RECORD_TTL
        ) : Record() {
            override val typeName: String
                get() = "SRV"
            override val value: String
                get() = "$priority $weight $port ${target.name}"


        }
    }
}

internal val String.q: String
    get() = "\"$this\""
