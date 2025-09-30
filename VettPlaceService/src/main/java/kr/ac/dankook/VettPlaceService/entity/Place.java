package kr.ac.dankook.VettPlaceService.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "place")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PlaceOperatingHour> operatingHours = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private PlaceCategory category;

    @Builder
    public Place(boolean isParking, String maxSizeInfo,
                    String regionCode, String addFeeInfo,
                    boolean isInside, boolean isOutside, String feeInfo, String restrictionInfo,
                    double longitude, double latitude, String phoneNumber,
                    String address, String placeName, PlaceCategory category) {
        this.isParking = isParking;
        this.maxSizeInfo = maxSizeInfo;
        this.longitude = longitude;
        this.latitude = latitude;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.placeName = placeName;
        this.category = category;
        this.isInside = isInside;
        this.isOutside = isOutside;
        this.feeInfo = feeInfo;
        this.restrictionInfo = restrictionInfo;
        this.regionCode = regionCode;
        this.addFeeInfo = addFeeInfo;
    }
}
