package com.survey.hzyanglili1.mysurvey.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hzyanglili1 on 2016/12/26.
 */

public class TimeUtil {

    /**
     * buffer有效  返回true
     * @param   bufferTime
     * @param   changeTime
     * @return
     */
    public static Boolean compareTime(String bufferTime,String changeTime){
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date1 = formatter.parse(bufferTime);
            Date date2 = formatter.parse(changeTime);

            if (date2.before(date1)) return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String getCurTime(){
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date curDate=new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);

        return str;
    }
}
