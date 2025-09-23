package kr.ac.dankook.VettPlaceService.repository;

import kr.ac.dankook.VettPlaceService.entity.PlaceOperatingHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PlaceOperatingHourRepository extends JpaRepository<PlaceOperatingHour,Long> {
    @Query("select poh from PlaceOperatingHour poh JOIN FETCH poh.place p where p.id in :placeIds")
    List<PlaceOperatingHour> findByPlaceIdIn(@Param("placeIds") Set<Long> placeIds);
}
