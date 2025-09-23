package kr.ac.dankook.VettPlaceService.dto.response;

import kr.ac.dankook.VettPlaceService.entity.DayType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Builder
public class PlaceOperatingHourResponse {

    private DayType dayType;
    private LocalTime openTime;
    private LocalTime closeTime;
    private boolean isOpen;
}
