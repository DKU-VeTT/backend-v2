package kr.ac.dankook.VettPlaceService.service;

import kr.ac.dankook.VettPlaceService.dto.response.PlaceDistResponse;
import kr.ac.dankook.VettPlaceService.dto.response.PlaceResponse;
import kr.ac.dankook.VettPlaceService.entity.Place;
import kr.ac.dankook.VettPlaceService.entity.PlaceCategory;
import kr.ac.dankook.VettPlaceService.entity.PlaceOperatingHour;
import kr.ac.dankook.VettPlaceService.repository.PlaceOperatingHourRepository;
import kr.ac.dankook.VettPlaceService.repository.PlaceRepository;
import kr.ac.dankook.VettPlaceService.util.DateUtil;
import kr.ac.dankook.VettPlaceService.util.converter.PlaceEntityConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceOperatingHourRepository placeOperatingHourRepository;

    public List<PlaceResponse> getAllPlace(Pageable pageable){
        // Page original Entity
        Page<Place> allPlaces = placeRepository.findAll(pageable);

        // PlaceIds Set
        Set<Long> placeIds = allPlaces.stream()
                .map(Place::getId)
                .collect(Collectors.toSet());
        // Get OperatingHour IN query & Fetch Join
        List<PlaceOperatingHour> hours = placeOperatingHourRepository
                .findByPlaceIdIn(placeIds);
        Map<Long, List<PlaceOperatingHour>> hoursMap =
                // if not fetch join ho.getPlace() -> N+1 problem
                hours.stream().collect(Collectors.groupingBy(ho -> ho.getPlace().getId()));

        return allPlaces.stream().map(item ->
                PlaceEntityConverter.convertToPlaceResponse(
                        item,true,hoursMap.getOrDefault(item.getId(), List.of()))).toList();
    }

    public List<PlaceResponse> getPlacesByKeyword(String keyword){
        List<Place> places = placeRepository.findByKeyword(keyword);
        return places.stream().map(item -> PlaceEntityConverter
                        .convertToPlaceResponse(item,false)).toList();
    }

    public List<PlaceDistResponse> getPlaceOrderByDist(PlaceCategory category, double desLatitude, double desLongitude) {

        String cat = category == null ? null : category.name();
        List<Object[]> rows = placeRepository.findNearestPlaceIds(cat, desLatitude, desLongitude, 10.0);
        Map<Long, Double> distanceMap = rows.stream()
                .collect(Collectors.toMap(
                        r -> ((Number) r[0]).longValue(),
                        r -> ((Number) r[1]).doubleValue()
        ));
        Set<Long> ids = distanceMap.keySet();
        if (ids.isEmpty()) return List.of();
        List<Place> places = placeRepository.findPlacesByIdsWithFetchJoin(ids);

        return places.stream()
                .map(place -> {
                    double distance = distanceMap.get(place.getId());
                    String distStr = distance >= 1
                            ? String.format("%.1fkm", distance)
                            : String.format("%.0fm", distance * 1000);
                    return PlaceDistResponse.builder()
                            .placeResponse(PlaceEntityConverter.convertToPlaceResponse(place,true))
                            .distance(distance)
                            .distanceStringFormat(distStr)
                            .build();
                })
                .sorted(Comparator.comparingDouble(PlaceDistResponse::getDistance)) // 안전하게 정렬
                .toList();
    }

    public List<PlaceResponse> getPlaceByFilter(PlaceCategory category,String regionCode,
            boolean isParking,boolean isOpen,boolean isInside,boolean isOutside){

        return placeRepository.findByCategoryAndFilters(category, regionCode, isInside, isOutside, isParking)
                .stream()
                .filter(item -> !isOpen || DateUtil.isOpenNow(item.getOperatingHours()))
                .map(item -> PlaceEntityConverter.convertToPlaceResponse(item, true))
                .toList();
    }
}