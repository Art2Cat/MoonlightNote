package com.art2cat.dev.moonlightnote.Utils;

import com.art2cat.dev.moonlightnote.Model.Moonlight;

/**
 * Created by Rorschach
 * on 12/14/16 9:28 PM.
 */

public class MoonlightEncryptUtils {

    public static Moonlight decryptMoonlight(Moonlight moonlight) {

        return decrypt(moonlight);
    }

    public static Moonlight encryptMoonlight(Moonlight moonlight) {

        return encrypt(moonlight);
    }

    private static Moonlight encrypt(Moonlight moonlight) {
        String[] metadata = getMetadata(moonlight);

        try {

            if (metadata[0] != null) {
                moonlight.setTitle(AESUtils.encrypt(metadata[0]));
            }

            if (metadata[1] != null) {
                moonlight.setContent(AESUtils.encrypt(metadata[1]));
            }

            if (metadata[2] != null) {
                moonlight.setImageUrl(AESUtils.encrypt(metadata[2]));
            }

            if (metadata[3] != null) {
                moonlight.setAudioUrl(AESUtils.encrypt(metadata[3]));
            }

            if (metadata[4] != null) {
                moonlight.setImageName(AESUtils.encrypt(metadata[4]));
            }

            if (metadata[5] != null) {
                moonlight.setAudioName(AESUtils.encrypt(metadata[5]));
            }

            return moonlight;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Moonlight decrypt(Moonlight moonlight) {

        String[] metadata = getMetadata(moonlight);

        try {

            if (metadata[0] != null) {
                moonlight.setTitle(AESUtils.decrypt(metadata[0]));
            }

            if (metadata[1] != null) {
                moonlight.setContent(AESUtils.decrypt(metadata[1]));
            }

            if (metadata[2] != null) {
                moonlight.setImageUrl(AESUtils.decrypt(metadata[2]));
            }

            if (metadata[3] != null) {
                moonlight.setAudioUrl(AESUtils.decrypt(metadata[3]));
            }

            if (metadata[4] != null) {
                moonlight.setImageName(AESUtils.decrypt(metadata[4]));
            }

            if (metadata[5] != null) {
                moonlight.setAudioName(AESUtils.decrypt(metadata[5]));
            }

            return moonlight;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String[] getMetadata(Moonlight moonlight) {
        String title = moonlight.getTitle();
        String content = moonlight.getContent();
        String imageUrl = moonlight.getImageUrl();
        String audioUrl = moonlight.getAudioUrl();
        String imageName = moonlight.getImageName();
        String audioName = moonlight.getAudioName();

        return new String[]{title, content, imageUrl, audioUrl, imageName, audioName};
    }


}
