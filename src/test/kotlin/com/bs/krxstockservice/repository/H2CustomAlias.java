package com.bs.krxstockservice.repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class H2CustomAlias {
    public static String YEARWEEK(String date){
        if (date.length() < 8) return "";
        DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate locDate = LocalDate.parse(date, yyyyMMdd);

        TemporalField weekOfWeekBasedYear = WeekFields.of(Locale.KOREAN).weekOfWeekBasedYear();
        //get monday from arg
        LocalDate startOfWeek = locDate.with(DayOfWeek.MONDAY);
        //get week count
        int weekCount = startOfWeek.get(weekOfWeekBasedYear);
        // generate  'year of monday' + 'weekCount'
        return String.format("%02d", startOfWeek.getYear()).concat(String.valueOf(weekCount));
    }
}
