/**
 * AES加密工具
 */
package com.art2cat.dev.moonlightnote.utils


import android.text.TextUtils
import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and

/**
 * Created by art2cat
 * on 7/25/16.
 */
open class AESUtils {


    companion object {

        private val TAG = "AESUtils"
        private val HEX = "0123456789ABCDEF"
        private val CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding"
        private val AES = "AES"
        private val PBKDF2WITHHMACSHA1 = "PBKDF2WithHmacSHA1"
        private val ASCII = "ASCII"
        private val HEX_ARRAY = HEX.toCharArray()
        private val SALT_LENGTH = 32
        private val KEY_LENGTH = 256
        private val ITERATION_COUNT = 32 //迭代次数越大，计算耗时越长，会造成UI卡顿（高于100）

        /**
         * 生成AES密钥

         * @return 密钥
         */
        fun generateKey(): String {
            val localSecureRandom = SecureRandom()
            val bytesKey = ByteArray(SALT_LENGTH)
            localSecureRandom.nextBytes(bytesKey)
            return Base64.encodeToString(bytesKey, Base64.CRLF)
        }

        /**
         * 针对随机生成的密钥进行处理

         * @param key 密钥
         * *
         * @return 处理后密钥
         * *
         * @throws Exception
         */
        @Throws(Exception::class)
        private fun getRawKey(key: CharArray): SecretKeySpec {
            val salt = ByteArray(SALT_LENGTH)
            val keySpec = PBEKeySpec(key, salt,
                    ITERATION_COUNT, KEY_LENGTH)
            val keyFactory = SecretKeyFactory
                    .getInstance(PBKDF2WITHHMACSHA1)
            val keyBytes = keyFactory.generateSecret(keySpec).getEncoded()
            return SecretKeySpec(keyBytes, AES)
        }

        /**
         * AES加密外部函数

         * @param key         AES Key
         * *
         * @param unencrypted 需要加密数据
         * *
         * @return 加密后数据
         */
        fun encrypt(key: String, unencrypted: String): String? {
            if (TextUtils.isEmpty(unencrypted)) {
                return unencrypted
            }
            try {
                val result = encrypt(key, unencrypted.toByteArray())
                return Base64.encodeToString(result, Base64.CRLF)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        /**
         * AES加密内部处理函数

         * @param key   AES Key
         * *
         * @param bytes 需要加密的字节流
         * *
         * @return 加密后的字节流
         * *
         * @throws Exception
         */
        @Throws(Exception::class)
        private fun encrypt(key: String, bytes: ByteArray): ByteArray {
            val secretKeySpec = getRawKey(key.toCharArray())
            val cipher = Cipher.getInstance(CBC_PKCS5_PADDING)
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, IvParameterSpec(ByteArray(cipher.getBlockSize())))
            return cipher.doFinal(bytes)
        }

        /**
         * AES解密外部函数

         * @param key       AES Key
         * *
         * @param encrypted 已加密数据
         * *
         * @return 未加密数据
         */
        fun decrypt(key: String, encrypted: String): String? {
            if (TextUtils.isEmpty(encrypted)) {
                return encrypted
            }
            try {
                val enc = Base64.decode(encrypted, Base64.CRLF)
                val result = decrypt(key, enc)
                return String(result)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        /**
         * AES解密内部处理函数

         * @param key   AES Key
         * *
         * @param bytes 需要解密的字节流
         * *
         * @return 解密后的字节流
         * *
         * @throws Exception
         */
        @Throws(Exception::class)
        private fun decrypt(key: String, bytes: ByteArray): ByteArray {
            val secretKeySpec = getRawKey(key.toCharArray())
            val cipher = Cipher.getInstance(CBC_PKCS5_PADDING)
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, IvParameterSpec(ByteArray(cipher.getBlockSize())))
            return cipher.doFinal(bytes)
        }


        private fun byteArrayToHexString(bytes: ByteArray): String {
            val hexChars = CharArray(bytes.size * 2)
            for (j in bytes.indices) {
                val v = bytes[j] and 0xFF.toByte()
                hexChars[j * 2] = HEX_ARRAY[v.plus(4)]
                hexChars[j * 2 + 1] = HEX_ARRAY[(v and 0x0F).toInt()]
            }
            return String(hexChars)
        }

        private fun hexStringToByteArray(s: String): ByteArray {
            val len = s.length
            val data = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                data[i / 2] = ((Character.digit(s.toInt(i), 16) shl 4) + Character.digit(s.toInt(i + 1), 16)) as Byte
                i += 2
            }
            return data
        }
    }
}
