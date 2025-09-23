package kr.ac.dankook.VettPlaceService.util.converter;

import kr.ac.dankook.VettPlaceService.dto.response.PlaceOperatingHourResponse;
import kr.ac.dankook.VettPlaceService.dto.response.PlaceResponse;
import kr.ac.dankook.VettPlaceService.entity.Place;
import kr.ac.dankook.VettPlaceService.entity.PlaceOperatingHour;
import kr.ac.dankook.VettPlaceService.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class PlaceEntityConverter {


    public static PlaceResponse convertToPlaceResponse(
            Place place,
            boolean isIncludeOperatingHours
    ) {
        List<PlaceOperatingHour> hours = isIncludeOperatingHours
                ? new ArrayList<>(place.getOperatingHours())
                : List.of();
        return convertToPlaceResponse(place, isIncludeOperatingHours, hours);
    }

    public static PlaceResponse convertToPlaceResponse(
            Place place,
            boolean isIncludeOperatingHours,
            List<PlaceOperatingHour> hours
    ) {
        PlaceResponse response = convertToPlaceResponseDto(place);
        if (!isIncludeOperatingHours) return response;

        List<PlaceOperatingHourResponse> operatingHours = hours.stream()
                .map(PlaceEntityConverter::convertToPlaceOperatingHoursResponseDto)
                .toList();
        response.setOperatingHours(operatingHours);
        response.setOpen(DateUtil.isOpenNow(hours));
        return response;
    }

    public static PlaceResponse convertToPlaceResponseDto(Place place){
        return PlaceResponse.builder()
                .id(place.getId())
                .addFeeInfo(place.getAddFeeInfo())
                .placeName(place.getPlaceName())
                .address(place.getAddress())
                .phoneNumber(place.getPhoneNumber())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .maxSizeInfo(place.getMaxSizeInfo())
                .isParking(place.isParking())
                .isInside(place.isInside())
                .isOutside(place.isOutside())
                .feeInfo(place.getFeeInfo())
                .restrictionInfo(place.getRestrictionInfo())
                .regionCode(place.getRegionCode())
                .category(place.getCategory()).build();
    }

    public static PlaceOperatingHourResponse convertToPlaceOperatingHoursResponseDto(PlaceOperatingHour hours){
        return PlaceOperatingHourResponse.builder()
                .dayType(hours.getDayType())
                .openTime(hours.getOpenTime())
                .closeTime(hours.getCloseTime())
                .isOpen(hours.isOpen()).build();
    }
}
