package com.xana.mikochat.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 格式化时间
 */
public class TimeUtil {
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yy-MM-dd", Locale.ENGLISH);
    public static String getString(Date date){
        return FORMAT.format(new Date());
    }
}
