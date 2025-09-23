package kr.ac.dankook.VettPlaceService.repository;

import kr.ac.dankook.VettPlaceService.entity.Place;
import kr.ac.dankook.VettPlaceService.entity.PlaceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    // Required index
    @Query("""
        SELECT distinct p FROM Place p
        LEFT JOIN FETCH p.operatingHours
        WHERE p.category = :category
          AND (:regionCode IS NULL OR p.regionCode = :regionCode)
          AND (:isInside = false OR p.isInside = true)
          AND (:isOutside = false OR p.isOutside = true)
          AND (:isParking = false OR p.isParking = true)
    """)
    List<Place> findByCategoryAndFilters(
            @Param("category") PlaceCategory category,
            @Param("regionCode") String regionCode,
            @Param("isInside") boolean isInside,
            @Param("isOutside") boolean isOutside,
            @Param("isParking") boolean isParking
    );

    @Query("select p from Place p JOIN FETCH p.operatingHours where p.id = :placeId")
    Optional<Place> findPlaceByIdWithFetchJoin(@Param("placeId") Long placeId);

    @Query("select p from Place p " +
            "where p.placeName like %:keyword% or p.address like %:keyword%")
    List<Place> findByKeyword(String keyword);

    @Query("select p from Place p JOIN FETCH p.operatingHours where p.id IN :ids")
    List<Place> findPlacesByIdsWithFetchJoin(@Param("ids") Set<Long> ids);

    @Query(value = """
        SELECT p.id,
               ST_Distance_Sphere(
                   point(p.longitude, p.latitude),
                   point(:lon, :lat)
               ) / 1000 AS distance
        FROM place p
        WHERE ST_Distance_Sphere(
                  point(p.longitude, p.latitude),
                  point(:lon, :lat)
              ) <= :radius * 1000
          AND (:category IS NULL OR p.category = :category)
        ORDER BY distance ASC
        LIMIT 30
    """, nativeQuery = true)
    List<Object[]> findNearestPlaceIds(
            @Param("category") String category,
            @Param("lat") double latitude,
            @Param("lon") double longitude,
            @Param("radius") double radiusKm
    );
}
