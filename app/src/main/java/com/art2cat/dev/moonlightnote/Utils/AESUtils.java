package com.art2cat.dev.moonlightnote.Utils;


import android.util.Base64;


import com.art2cat.dev.moonlightnote.Model.Constants;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by art2cat
 * on 7/25/16.
 */
public class AESUtils {

    /**
     * @param sSrc 需要加密的字符串
     * @return 返回加密后的字符串
     * @throws Exception
     */
    public static String encrypt(String sSrc) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = Constants.AES_KEY.getBytes();
        SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(Constants.IV_PARAMERER.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        return Base64.encodeToString(encrypted, Base64.CRLF);// 此处使用BASE64做转码。
    }

    /**
     * @param sSrc 需要解密的字符串
     * @return 返回解密后的字符串
     * @throws Exception
     */
    public static String decrypt(String sSrc) throws Exception {
        try {
            byte[] raw = Constants.AES_KEY.getBytes("ASCII");
            SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(Constants.IV_PARAMERER.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            byte[] encrypted1 = Base64.decode(sSrc, Base64.CRLF);// 先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }
}
