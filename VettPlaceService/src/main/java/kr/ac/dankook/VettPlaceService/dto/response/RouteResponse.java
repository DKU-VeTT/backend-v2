package kr.ac.dankook.VettPlaceService.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RouteResponse {
    private double statLatitude;
    private double statLongitude;
    private double desLatitude;
    private double desLongitude;
    private String distance;
    private int time;
    private int taxiFare;
    private int tollFare;
}
