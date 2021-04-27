package com.ptda.imiser.helper;

import java.text.SimpleDateFormat;

public class DateUtilCustom {
    public static String dateTodayCustom() {
        long date = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(date);
    }

    public static String getDate(String date) {
        String returnDate[] = date.split("/");
        String month = returnDate[1];
        String year = returnDate[2];
        String monthYear = month + year;
        return monthYear;
    }
}
