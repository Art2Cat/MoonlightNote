package com.art2cat.dev.moonlightnote;



import com.art2cat.dev.moonlightnote.model.Moonlight;
import com.art2cat.dev.moonlightnote.utils.MoonlightEncryptUtils;


import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by huang.yiming
 * on 6/28/2017.
 */

public class MoonlightEncryptTest extends TestCase{

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
        MoonlightEncryptUtils moonlightEncryptUtils = new MoonlightEncryptUtils();
        moonlightEncryptUtils.setKey("12345678");
        assert moonlight != null;
        System.out.println(moonlight.getContent());
        Moonlight moonlight1 = moonlightEncryptUtils.encrypt("12345678",moonlight);
        assert moonlight1 != null;
        System.out.println(moonlight1.getContent());
    }
}
