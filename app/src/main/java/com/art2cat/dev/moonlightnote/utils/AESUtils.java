/**
 * AES加密工具
 */
package com.art2cat.dev.moonlightnote.utils;


import android.text.TextUtils;
import android.util.Base64;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by art2cat on 7/25/16.
 */
public class AESUtils {

  private static final String TAG = "AESUtils";
  private static final String HEX = "0123456789ABCDEF";
  private static final String CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";
  private static final String AES = "AES";
  private static final String PBKDF2WITHHMACSHA1 = "PBKDF2WithHmacSHA1";
  private static final String ASCII = "ASCII";
  private static final char[] HEX_ARRAY = HEX.toCharArray();
  private static final int SALT_LENGTH = 32;
  private static final int KEY_LENGTH = 256;
  private static final int ITERATION_COUNT = 32; //迭代次数越大，计算耗时越长，会造成UI卡顿（高于100）

  /**
   * 生成AES密钥
   *
   * @return 密钥
   */
  public static String generateKey() {
    SecureRandom localSecureRandom = new SecureRandom();
    byte[] bytesKey = new byte[SALT_LENGTH];
    localSecureRandom.nextBytes(bytesKey);
    return Base64.encodeToString(bytesKey, Base64.CRLF);
  }

  /**
   * 针对随机生成的密钥进行处理
   *
   * @param key 密钥
   * @return 处理后密钥
   */
  private static SecretKeySpec getRawKey(char[] key) throws Exception {
    byte[] salt = new byte[SALT_LENGTH];
    KeySpec keySpec = new PBEKeySpec(key, salt,
        ITERATION_COUNT, KEY_LENGTH);
    SecretKeyFactory keyFactory = SecretKeyFactory
        .getInstance(PBKDF2WITHHMACSHA1);
    byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
    return new SecretKeySpec(keyBytes, AES);
  }

  /**
   * AES加密外部函数
   *
   * @param key AES Key
   * @param unencrypted 需要加密数据
   * @return 加密后数据
   */
  public static String encrypt(String key, String unencrypted) {
    if (TextUtils.isEmpty(unencrypted)) {
      return unencrypted;
    }
    try {
      byte[] result = encrypt(key, unencrypted.getBytes());
      return Base64.encodeToString(result, Base64.CRLF);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * AES加密内部处理函数
   *
   * @param key AES Key
   * @param bytes 需要加密的字节流
   * @return 加密后的字节流
   */
  private static byte[] encrypt(String key, byte[] bytes) throws Exception {
    SecretKeySpec secretKeySpec = getRawKey(key.toCharArray());
    Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec,
        new IvParameterSpec(new byte[cipher.getBlockSize()]));
    return cipher.doFinal(bytes);
  }

  /**
   * AES解密外部函数
   *
   * @param key AES Key
   * @param encrypted 已加密数据
   * @return 未加密数据
   */
  public static String decrypt(String key, String encrypted) {
    if (TextUtils.isEmpty(encrypted)) {
      return encrypted;
    }
    try {
      byte[] enc = Base64.decode(encrypted, Base64.CRLF);
      byte[] result = decrypt(key, enc);
      return new String(result);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * AES解密内部处理函数
   *
   * @param key AES Key
   * @param bytes 需要解密的字节流
   * @return 解密后的字节流
   */
  private static byte[] decrypt(String key, byte[] bytes) throws Exception {
    SecretKeySpec secretKeySpec = getRawKey(key.toCharArray());
    Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec,
        new IvParameterSpec(new byte[cipher.getBlockSize()]));
    return cipher.doFinal(bytes);
  }


  private static String byteArrayToHexString(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }

  private static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
          + Character.digit(s.charAt(i + 1), 16));
    }
    return data;
  }
}
