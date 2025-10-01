package kr.ac.dankook.VettPlaceService.service;

import kr.ac.dankook.VettPlaceService.dto.response.BookmarkResponse;
import kr.ac.dankook.VettPlaceService.entity.Bookmark;
import kr.ac.dankook.VettPlaceService.entity.Place;
import kr.ac.dankook.VettPlaceService.error.ErrorCode;
import kr.ac.dankook.VettPlaceService.error.exception.CustomException;
import kr.ac.dankook.VettPlaceService.error.exception.EntityNotFoundException;
import kr.ac.dankook.VettPlaceService.repository.BookmarkRepository;
import kr.ac.dankook.VettPlaceService.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public void saveBookmark(Long placeId,String memberId){

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException("해당 장소를 찾을 수 없습니다."));
        if (bookmarkRepository.findByMemberIdAndPlace(memberId,place).isPresent()){
            throw new CustomException(ErrorCode.ALREADY_ADD_BOOKMARK);
        }
        Bookmark bookmark = Bookmark.builder()
                .memberId(memberId).place(place).build();
        bookmarkRepository.save(bookmark);
    }

    public void deleteBookmark(String memberId, Long bookmarkId){
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                        .orElseThrow(() -> new EntityNotFoundException("즐겨찾기 내역이 존재하지 않습니다."));
        if (!bookmark.getMemberId().equals(memberId)){
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        bookmarkRepository.deleteById(bookmarkId);
    }

    @Transactional
    public List<BookmarkResponse> getBookmarkListByMemberId(String memberId){
        List<Bookmark> bookmarks = bookmarkRepository.findByMemberId(memberId);
        return bookmarks.stream().map(BookmarkResponse::new).toList();
    }
}
