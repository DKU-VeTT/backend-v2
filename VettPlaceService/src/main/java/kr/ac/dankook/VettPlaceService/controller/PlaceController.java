package kr.ac.dankook.VettPlaceService.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.ac.dankook.VettPlaceService.dto.response.ApiResponse;
import kr.ac.dankook.VettPlaceService.dto.response.PlaceDistResponse;
import kr.ac.dankook.VettPlaceService.dto.response.PlaceResponse;
import kr.ac.dankook.VettPlaceService.entity.PlaceCategory;
import kr.ac.dankook.VettPlaceService.service.PlaceCacheService;
import kr.ac.dankook.VettPlaceService.service.PlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
@Slf4j
public class PlaceController {


    private final PlaceService placeService;
    private final PlaceCacheService placeCacheService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PlaceResponse>>> getAllPlaces(
        Pageable pageable
    ) {
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                placeService.getAllPlace(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlaceResponse>> getPlaceById(
            @PathVariable Long id) throws JsonProcessingException {
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                placeCacheService.getPlaceById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PlaceResponse>>> getVetPlacesByFilter(
            @RequestParam("keyword") String keyword
    ){
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                placeService.getPlacesByKeyword(keyword)));
    }

    @GetMapping("/dist")
    public ResponseEntity<ApiResponse<List<PlaceDistResponse>>> getVetPlaceByDist(
            @RequestParam(value = "category", required = false) PlaceCategory category,
            @RequestParam(value= "isOpen", required = false) boolean isOpen,
            @RequestParam(value = "latitude") double latitude,
            @RequestParam(value = "longitude") double longitude
    ){
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                placeService.getPlaceOrderByDist(category, isOpen, latitude, longitude)));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<PlaceResponse>>> getVetPlaceByFilter(
            @RequestParam("category") PlaceCategory category,
            @RequestParam(value = "region", required = false) String regionCode,
            @RequestParam(value = "isParking", required = false) boolean isParking,
            @RequestParam(value = "isOpen", required = false) boolean isOpen,
            @RequestParam(value = "isInside", required = false) boolean isInside,
            @RequestParam(value = "isOutside", required = false) boolean isOutside
    ){
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                placeService.getPlaceByFilter(
                        category,regionCode,isParking,isOpen,isInside,isOutside)));
    }
}
