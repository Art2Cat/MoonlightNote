/**
 * AES加密工具
 */
package com.art2cat.dev.moonlightnote.Utils;


import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by art2cat
 * on 7/25/16.
 */
class AESUtils {


    private static final String AES_KEY = "0123456789abcdef";   //此为AESKey（可修改）
    private static final String IV_PARAMERER = "1020304050607080";    //AES偏移量 （可修改）
    private static final String HEX = "0123456789ABCDEF";
    //AES是加密方式 CBC是工作模式 PKCS5Padding是填充模式
    private static final String CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";
    private static final String AES = "AES";//AES 加密
    private static final String SHA1PRNG = "SHA1PRNG";// SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法
    private static final char[] HEX_ARRAY = HEX.toCharArray();

    /**
     * 生成AES密钥
     *
     * @return 密钥
     */
    public static String generateKey() {
        try {
            SecureRandom localSecureRandom = SecureRandom.getInstance("SHA1RPNG");
            byte[] bytesKey = new byte[20];
            localSecureRandom.nextBytes(bytesKey);
            return byteArrayToHexString(bytesKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对密钥进行处理
     *
     * @param seed 密钥
     * @return 处理后密钥
     * @throws Exception
     */
    private static byte[] getRawKey(byte[] seed) throws Exception {
        SecureRandom secureRandom;
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
        // 在4.2以上版本中，SecureRandom获取方式发生了改变
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            secureRandom = SecureRandom.getInstance(SHA1PRNG, "Crypto");
        } else {
            secureRandom = SecureRandom.getInstance(SHA1PRNG);
        }

        secureRandom.setSeed(seed);
        // 可选256 bits or 128 bits,192bits
        // AES中128位密钥版本有10个加密循环，192比特密钥版本有12个加密循环，256比特密钥版本则有14个加密循环。
        keyGenerator.init(128, secureRandom);

        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey.getEncoded();
    }

    /**
     * AES加密外部函数
     *
     * @param key         AES Key
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
     * @param key   AES Key
     * @param bytes 需要加密的字节流
     * @return 加密后的字节流
     * @throws Exception
     */
    private static byte[] encrypt(String key, byte[] bytes) throws Exception {
        byte[] raw = getRawKey(key.getBytes());
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, AES);
        Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        return cipher.doFinal(bytes);
    }

    /**
     * AES解密外部函数
     *
     * @param key       AES Key
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
     * @param key   AES Key
     * @param bytes 需要解密的字节流
     * @return 解密后的字节流
     * @throws Exception
     */
    private static byte[] decrypt(String key, byte[] bytes) throws Exception {
        byte[] raw = getRawKey(key.getBytes());
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, AES);
        Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        return cipher.doFinal(bytes);
    }

    /**
     * @param sSrc 需要加密的字符串
     * @return 返回加密后的字符串
     * @throws Exception
     */
    static String encrypt(String sSrc) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = AES_KEY.getBytes();
        SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(IV_PARAMERER.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        return Base64.encodeToString(encrypted, Base64.CRLF);// 此处使用BASE64做转码。
    }

    /**
     * @param sSrc 需要解密的字符串
     * @return 返回解密后的字符串
     * @throws Exception
     */
    @Nullable
    static String decrypt(String sSrc) throws Exception {
        try {
            byte[] raw = AES_KEY.getBytes("ASCII");
            SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMERER.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            byte[] encrypted1 = Base64.decode(sSrc, Base64.CRLF);// 先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, "utf-8");
        } catch (Exception ex) {
            return null;
        }
    }

    public static String byteArrayToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
