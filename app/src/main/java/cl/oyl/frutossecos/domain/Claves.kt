package cl.oyl.frutossecos.domain

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object Claves {
    fun crearSal(): String = Base64.getEncoder().encodeToString(ByteArray(16).also { SecureRandom().nextBytes(it) })
    fun calcular(clave: String, sal: String): String {
        val spec = PBEKeySpec(clave.toCharArray(), Base64.getDecoder().decode(sal), 120_000, 256)
        return try {
            Base64.getEncoder().encodeToString(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded)
        } finally { spec.clearPassword() }
    }
    fun coincide(clave: String, sal: String, hash: String): Boolean = MessageDigest.isEqual(calcular(clave, sal).toByteArray(), hash.toByteArray())
}
