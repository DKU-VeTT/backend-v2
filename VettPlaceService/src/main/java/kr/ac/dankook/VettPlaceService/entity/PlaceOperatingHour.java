package kr.ac.dankook.VettPlaceService.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "place_operating_hour")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceOperatingHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DayType dayType;

    private LocalTime openTime;
    private LocalTime closeTime;
    private boolean isOpen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "place_id")
    @Setter
    private Place place;

    @Builder
    public PlaceOperatingHour(DayType dayType, LocalTime openTime,
                                 LocalTime closeTime, boolean isOpen, Place place) {
        this.dayType = dayType;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.isOpen = isOpen;
        this.place = place;
    }
}
