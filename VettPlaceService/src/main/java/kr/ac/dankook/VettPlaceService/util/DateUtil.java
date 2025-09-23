package kr.ac.dankook.VettPlaceService.util;

import kr.ac.dankook.VettPlaceService.entity.DayType;
import kr.ac.dankook.VettPlaceService.entity.PlaceOperatingHour;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class DateUtil {

    public static boolean isOpenNow(
            List<PlaceOperatingHour> hours) {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek today = LocalDateTime.now().getDayOfWeek();

        DayType currentDayType = Holiday.isHoliday(now.toLocalDate()) ?
                DayType.HOLIDAY
                : switch (today) {
            case MONDAY -> DayType.MON;
            case TUESDAY -> DayType.TUE;
            case WEDNESDAY -> DayType.WED;
            case THURSDAY -> DayType.THU;
            case FRIDAY -> DayType.FRI;
            case SATURDAY -> DayType.SAT;
            case SUNDAY -> DayType.SUN;
        };
        LocalTime currentTime = now.toLocalTime();
        return hours.stream()
                .filter(h -> h.getDayType() == currentDayType)
                .filter(PlaceOperatingHour::isOpen)
                .anyMatch(h ->
                        h.getOpenTime() != null &&
                                h.getCloseTime() != null &&
                                !currentTime.isBefore(h.getOpenTime()) &&
                                currentTime.isBefore(h.getCloseTime())
                );
    }
}
