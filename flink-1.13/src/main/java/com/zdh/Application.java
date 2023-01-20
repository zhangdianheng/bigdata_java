package com.zdh;

import com.zdh.stream.DateUtils;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * @author zdh
 * @date 2022-05-23 14:32
 * @Version 1.0
 */
public class Application {
    public static void main(String[] args) {
//        System.out.println(DateUtil.current());
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Long aLong = DateUtils.parseStringToLong("1653615313782", pattern, 8, ChronoUnit.HOURS);
        System.out.println(aLong);
    }
}
