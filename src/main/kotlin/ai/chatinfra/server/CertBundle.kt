package ai.chatinfra.ai.chatinfra.server

import ai.chatinfra.bashscript.Bash
import ai.chatinfra.bashscript.combine

import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.Instant

 interface CertBundle {
     val filename: String
     val subject: String
     val commonName: String
     val serialNumber: String
     val issuer: String
     val endDate: Instant
     val key: String
     val chain: String

     val hostname: Hostname
        get() = Hostname(commonName)

     val domain: String
        get() = commonName.substringAfter("*.", commonName)

    //     val filename: String
//        get() = commonName.replace("*.", "wildcard.")
    val chainFilename: String
        get() = "$commonName.$chainSuffix"
    val keyFilename: String
        get() = "$commonName.$keySuffix"
    val combinedFilename: String
        get() = "$commonName.$combinedSuffix"

    companion object {
        val chainSuffix = "chain.pem"
        val keySuffix = "key.pem"
        val combinedSuffix = "combined.pem"
        val csrSuffix = "csr.pem"
    }
}


 fun CertBundle.toCombinedPemFile(path: String): ConfFile = ConfFile("$path/$combinedFilename", listOf(
    toKeyConfFile(""),
    toChainConfFile(""),
).joinToString("\n") { it.content })

fun Map.Entry<Hostname, CertBundle>.toAllCertConfFiles(path: String): List<ConfFile> = value.toAllCertConfFiles(path)


 fun CertBundle.toAllCertConfFiles(path: String): List<ConfFile> = listOf(
    toKeyConfFile("$path/$keyFilename"),
    toChainConfFile("$path/$chainFilename"),
    toCombinedPemFile("$path/$combinedFilename"),
)

 fun CertBundle.toChainConfFile(filename: String): ConfFile = ConfFile(filename = filename, content = chain)
 fun CertBundle.toKeyConfFile(filename: String): ConfFile = ConfFile(filename = filename, content = key)


 data class FakeCertBundle(
    override val filename: String = "",
    override val subject: String = "",
    override val commonName: String = "",
    override val serialNumber: String = "",
    override val issuer: String = "",
    override val endDate: Instant = now(),
    override val key: String = "",
    override val chain: String = ""
) : CertBundle


//fun Map<Hostname, CertBundle>.confAllFiles(path: String): List<ConfFile> = flatMap {
//    listOf(
//        it.value.toKeyConfFile("$path/${it.value.keyFilename}"),
//        it.value.toChainConfFile("$path/${it.value.chainFilename}"),
//        it.value.toCombinedPemFile("$path/${it.value.combinedFilename}"),
//    )
//}

fun Map<Hostname, CertBundle>.bashDeliverCerts(path: String): Bash = values.flatMap { it.toAllCertConfFiles(path) }.map { it.bashDeliverConf() }.combine()
