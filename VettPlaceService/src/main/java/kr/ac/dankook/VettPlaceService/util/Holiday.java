package kr.ac.dankook.VettPlaceService.util;

import java.time.LocalDate;

public enum Holiday {
    _2025_01_01,
    _2025_01_27,
    _2025_01_28,
    _2025_01_29,
    _2025_01_30,
    _2025_03_01,
    _2025_03_03,
    _2025_05_05,
    _2025_05_06,
    _2025_06_03,
    _2025_06_06,
    _2025_08_15,
    _2025_10_03,
    _2025_10_05,
    _2025_10_06,
    _2025_10_07,
    _2025_10_08,
    _2025_10_09,
    _2025_12_25;

    public static boolean isHoliday(LocalDate date) {
        String enumKey = "_" + date.toString().replaceAll("-","_");
        try {
            Holiday.valueOf(enumKey);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
