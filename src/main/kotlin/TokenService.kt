package no.nav.helsearbeidsgiver

import io.ktor.server.plugins.BadRequestException
import no.nav.helsearbeidsgiver.KubeCtlClient.getServices

enum class SecretType {
    Maskinporten,
    Altinn,
    Azure,
    TokenX,
    Aiven,
}

data class TokenResponse(
    val token: String,
    val erCached: Boolean,
)

class TokenService(
    val secretType: SecretType,
) {
    fun hentTokenResponse(serviceNavn: String): TokenResponse {
        val cachedSecret = SecretsCache.getValue(secretType, serviceNavn)

        val secret = cachedSecret ?: this.hentSecret(serviceNavn)

        val token = this.hentToken(secret)

        return TokenResponse(
            token = token,
            erCached = cachedSecret != null,
        )
    }

    private fun hentToken(secret: KubeSecret): String =
        when (secretType) {
            SecretType.Maskinporten -> hentMaskinportenToken(secret)
            SecretType.Altinn -> hentMaskinportenToken(secret).veksleTilAltinnToken()

            else -> throw NotImplementedError("Har ikke implementert den type secret ${secretType.name}")
        }

    private fun hentSecret(serviceNavn: String): KubeSecret {
        val kubeCtlClient = KubeCtlClient

        val navnListe = getServices(secretType)

        val targetServiceNavn =
            navnListe
                .find { it.contains(serviceNavn) }
                ?: throw BadRequestException("Fant ikke service. Må være en av disse:\n${navnListe.joinToString("\n")}")

        return kubeCtlClient.getSecrets(targetServiceNavn)
    }
}

// fun PipelineContext<Unit, ApplicationCall>.getMaskinportenToken(serviceNavn: String): String {
//    val secret = getSecret(serviceNavn)
//    return hentMaskinportenToken(secret)
// }
