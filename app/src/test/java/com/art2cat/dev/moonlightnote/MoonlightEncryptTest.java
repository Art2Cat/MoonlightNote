package com.art2cat.dev.moonlightnote;

import com.art2cat.dev.moonlightnote.model.Moonlight;
import com.art2cat.dev.moonlightnote.utils.MoonlightEncryptUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by huang.yiming
 * on 6/28/2017.
 */

public class MoonlightEncryptTest {

    private Moonlight moonlight;

    @Before
    public void init() {
        moonlight = new Moonlight();
        moonlight.setAudioName("test");
        moonlight.setAudioUrl("test_url");
        moonlight.setContent("fuck up test");
        moonlight.setTitle("test for test");
        moonlight.setId("id00101010100");
    }

    @Test
    public void testEncrypt() {
        init();
        MoonlightEncryptUtils moonlightEncryptUtils = MoonlightEncryptUtils.newInstance();
        moonlightEncryptUtils.setKey("12345678");
        Assert.assertNotNull(moonlight);
        System.out.println(moonlight.getContent());

        Moonlight encrypt = moonlightEncryptUtils.encryptMoonlight(moonlight);
        Assert.assertNotNull(encrypt);
        System.out.println(encrypt.getContent());

        Moonlight decrypt = moonlightEncryptUtils.decryptMoonlight(encrypt);
        Assert.assertNotNull(decrypt);
        Assert.assertEquals(moonlight.getContent(), decrypt.getContent());
    }
}
