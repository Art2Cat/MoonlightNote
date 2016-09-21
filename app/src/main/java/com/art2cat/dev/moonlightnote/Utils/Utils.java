package com.art2cat.dev.moonlightnote.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by art2cat
 * on 8/4/16.
 */
public class Utils {

    /**
     * 格式化日期
     *
     * @param date 日期
     * @return 格式化后日期
     */
    public static String dateFormat(Date date) {
        String pattern;
        if (Locale.getDefault() == Locale.CHINA) {
            pattern = "yyyy-MMM-dd, EE";
        } else {
            pattern = "EE, MMM dd, yyyy";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
        return formatter.format(date);
    }



}
