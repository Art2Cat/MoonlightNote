package com.art2cat.dev.moonlightnote.Utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.art2cat.dev.moonlightnote.Model.BusEvent;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.Model.Moonlight;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Rorschach
 * on 2016/11/16 14:23.
 */

public class BusEventUtils {

    public static void post(int flag, @Nullable String message) {
        BusEvent busEvent = new BusEvent();
        busEvent.setFlag(flag);
        if (message != null) {
            busEvent.setMessage(message);
        }
        EventBus.getDefault().post(busEvent);
    }

}
