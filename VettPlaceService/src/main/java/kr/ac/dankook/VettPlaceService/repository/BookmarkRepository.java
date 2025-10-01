package kr.ac.dankook.VettPlaceService.repository;

import kr.ac.dankook.VettPlaceService.entity.Bookmark;
import kr.ac.dankook.VettPlaceService.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {

    @Query("select b from Bookmark b JOIN FETCH b.place where b.memberId = :memberId")
    List<Bookmark> findByMemberId(@Param("memberId") String memberId);

    Optional<Bookmark> findByMemberIdAndPlace(String memberId, Place place);
}
