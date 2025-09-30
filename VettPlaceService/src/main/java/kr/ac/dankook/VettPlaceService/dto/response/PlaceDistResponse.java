package kr.ac.dankook.VettPlaceService.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlaceDistResponse {
    private PlaceResponse placeResponse;
    private double distance;
    private String distanceStringFormat;
}
