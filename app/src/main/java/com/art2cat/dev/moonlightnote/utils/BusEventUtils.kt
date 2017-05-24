package com.art2cat.dev.moonlightnote.utils

import com.art2cat.dev.moonlightnote.model.BusEvent
import com.art2cat.dev.moonlightnote.model.Moonlight
import org.greenrobot.eventbus.EventBus

/**
 * Created by Rorschach
 * on 24/05/2017 9:15 PM.
 */


open class BusEventUtils {

    companion object {

        /**
         * 发送事件

         * @param flag    事件类型
         * *
         * @param message 消息内容
         */
        fun post(flag: Int, message: String?) {
            val busEvent = BusEvent()
            if (message != null) {
                busEvent.message = message
            }
            post(busEvent, flag)
        }

        /**
         * 发送事件

         * @param flag      事件类型
         * *
         * @param moonlight 消息内容
         */
        fun post(moonlight: Moonlight?, flag: Int) {
            val busEvent = BusEvent()
            if (moonlight == null) {
                return
            }
            busEvent.moonlight = moonlight
            post(busEvent, flag)
        }

        private fun post(busEvent: BusEvent, flag: Int) {
            busEvent.flag = flag
            EventBus.getDefault().post(busEvent)
        }
    }
}
