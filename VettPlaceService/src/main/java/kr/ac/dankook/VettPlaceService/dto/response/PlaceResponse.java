package kr.ac.dankook.VettPlaceService.dto.response;

import kr.ac.dankook.VettPlaceService.entity.PlaceCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
public class PlaceResponse {
    private Long id;
    private String addFeeInfo;
    private String placeName;
    private String address;
    private String phoneNumber;
    private double latitude;
    private double longitude;
    private String maxSizeInfo;
    private boolean isParking;
    private boolean isInside;
    private boolean isOutside;
    private String feeInfo;
    private String restrictionInfo;
    private String regionCode;
    private PlaceCategory category;
    private boolean isOpen;
    private List<PlaceOperatingHourResponse> operatingHours;
}
