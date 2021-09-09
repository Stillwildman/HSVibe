package com.hsvibe.utilities

import android.util.Base64
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Created by Vincent on 2021/9/9.
 */
object AESHelper {

    private const val cipherMode = "AES/CBC/pkcs5padding"
    private const val key = "www.kcsys.com.tw"

    private fun createKey(): SecretKeySpec {
        var data: ByteArray? = null

        val sb = StringBuffer(16)

        sb.append(key)

        while (sb.length < 16) {
            sb.append("0")
        }
        if (sb.length > 16) {
            sb.setLength(16)
        }
        try {
            data = sb.toString().toByteArray(StandardCharsets.UTF_8)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return SecretKeySpec(data, "AES")
    }

    private fun decrypt(content: ByteArray): ByteArray? {
        try {
            val key = createKey()
            val cipher = Cipher.getInstance(cipherMode)
            cipher.init(Cipher.DECRYPT_MODE, key)
            return cipher.doFinal(content)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun decryptFromBase64(content: String): String {
        val decodedCipherText = Base64.decode(content.toByteArray(), Base64.DEFAULT)
        val decrypt = decrypt(decodedCipherText)
        var message = ""
        try {
            decrypt?.let {
                message = String(it, StandardCharsets.UTF_8)
            }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return message
    }
}