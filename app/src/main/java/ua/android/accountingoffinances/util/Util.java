package ua.android.accountingoffinances.util;

import java.util.Calendar;

/**
 * Created by Acer on 13.04.2018.
 */

public class Util {

    public static long[] getMonthBounds(long dateTimeMillis){
        Calendar dateStart = Calendar.getInstance();
        dateStart.setTimeInMillis(dateTimeMillis);
        int minDay = dateStart.getMinimum(Calendar.DAY_OF_MONTH);
        dateStart.set(Calendar.DAY_OF_MONTH, minDay);
        dateStart.set(Calendar.HOUR_OF_DAY, 0);
        dateStart.set(Calendar.MINUTE, 0);
        dateStart.set(Calendar.SECOND, 0);
        Calendar dateEnd = Calendar.getInstance();
        dateEnd.setTimeInMillis(dateTimeMillis);
        int maxDay = dateStart.getActualMaximum(Calendar.DAY_OF_MONTH);
        dateEnd.set(Calendar.DAY_OF_MONTH, maxDay);
        dateEnd.set(Calendar.HOUR_OF_DAY, 23);
        dateEnd.set(Calendar.MINUTE, 59);
        dateEnd.set(Calendar.SECOND, 59);
        long start = dateStart.getTimeInMillis();
        long end = dateEnd.getTimeInMillis();
        return new long[]{start, end};
    }

    public static long[] getDayBounds(long dateTimeMillis){
        Calendar firstDateStart = Calendar.getInstance();
        firstDateStart.setTimeInMillis(dateTimeMillis);
        firstDateStart.set(Calendar.HOUR_OF_DAY, 0);
        firstDateStart.set(Calendar.MINUTE, 0);
        firstDateStart.set(Calendar.SECOND, 0);
        Calendar firstDateEnd = Calendar.getInstance();
        firstDateEnd.setTimeInMillis(dateTimeMillis);
        firstDateEnd.set(Calendar.HOUR_OF_DAY, 23);
        firstDateEnd.set(Calendar.MINUTE, 59);
        firstDateEnd.set(Calendar.SECOND, 59);
        long start = firstDateStart.getTimeInMillis();
        long end = firstDateEnd.getTimeInMillis();
        return new long[]{start, end};
    }
}
