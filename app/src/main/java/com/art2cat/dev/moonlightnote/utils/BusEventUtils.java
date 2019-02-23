package com.art2cat.dev.moonlightnote.utils;

import android.support.annotation.Nullable;
import com.art2cat.dev.moonlightnote.model.BusEvent;
import com.art2cat.dev.moonlightnote.model.Moonlight;
import java.util.Objects;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Rorschach on 2016/11/16 14:23.
 */

public class BusEventUtils {

  /**
   * 发送事件
   *
   * @param flag 事件类型
   * @param message 消息内容
   */
  public static void post(int flag, @Nullable String message) {
    BusEvent busEvent = new BusEvent();
    if (Objects.nonNull(message)) {
      busEvent.setMessage(message);
    }
    post(busEvent, flag);
  }

  /**
   * 发送事件
   *
   * @param flag 事件类型
   * @param moonlight 消息内容
   */
  public static void post(Moonlight moonlight, int flag) {
    BusEvent busEvent = new BusEvent();
    if (Objects.isNull(moonlight)) {
      return;
    }
    busEvent.setMoonlight(moonlight);
    post(busEvent, flag);
  }

  private static void post(BusEvent busEvent, int flag) {
    busEvent.setFlag(flag);
    EventBus.getDefault().post(busEvent);
  }

}
