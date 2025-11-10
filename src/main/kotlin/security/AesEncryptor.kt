package com.overtheinfinite.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class AesEncryptor(
    // 설정 파일에서 32바이트 Base64 키를 주입받습니다.
    @Value("\${encryption.aes-key}")
    private val base64Key: String
) {
    // 💡 AES-256에 필요한 키 바이트 (32바이트)
    private val KEY_SIZE = 32
    // 💡 AES에서 표준으로 사용되는 IV 바이트 (16바이트)
    private val IV_SIZE = 16
    // 💡 알고리즘 정의: AES/CBC/PKCS5Padding (CBC 모드는 IV가 필수)
    private val ALGORITHM = "AES"
    private val TRANSFORMATION = "AES/CBC/PKCS5Padding"

    private val secretKey: SecretKeySpec = run {
        // Base64 키를 바이트 배열로 디코딩
        val keyBytes = Base64.getDecoder().decode(base64Key)
        if (keyBytes.size != KEY_SIZE) {
            throw IllegalArgumentException("AES key must be $KEY_SIZE bytes long after decoding.")
        }
        // SecretKeySpec 객체 생성
        SecretKeySpec(keyBytes, ALGORITHM)
    }

    /**
     * 데이터를 AES-256으로 암호화하고, IV와 암호화된 데이터를 Base64로 인코딩하여 반환합니다.
     * @param plainText 평문 문자열
     * @return "Base64(IV):Base64(Ciphertext)" 형식의 문자열
     */
    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = ByteArray(IV_SIZE).apply { SecureRandom().nextBytes(this) }
        val ivSpec = IvParameterSpec(iv)

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        // IV와 암호화된 데이터를 결합하여 전송하기 위해 Base64로 인코딩합니다.
        val ivBase64 = Base64.getEncoder().encodeToString(iv)
        val cipherTextBase64 = Base64.getEncoder().encodeToString(encryptedBytes)

        // IV와 암호문을 ":"로 구분하여 반환합니다.
        return "$ivBase64:$cipherTextBase64"
    }

    /**
     * "Base64(IV):Base64(Ciphertext)" 형식의 문자열을 복호화하여 평문을 반환합니다.
     * @param encryptedData 암호화된 문자열
     * @return 복호화된 평문 문자열
     */
    fun decrypt(encryptedData: String): String {
        val parts = encryptedData.split(":")
        if (parts.size != 2) {
            throw IllegalArgumentException("Invalid encrypted data format.")
        }

        // IV와 암호문 분리 및 Base64 디코딩
        val ivBytes = Base64.getDecoder().decode(parts[0])
        val cipherTextBytes = Base64.getDecoder().decode(parts[1])

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val ivSpec = IvParameterSpec(ivBytes)

        // 복호화 모드로 초기화
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        val decryptedBytes = cipher.doFinal(cipherTextBytes)

        return String(decryptedBytes, Charsets.UTF_8)
    }
}