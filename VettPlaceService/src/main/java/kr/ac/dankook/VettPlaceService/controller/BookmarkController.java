package kr.ac.dankook.VettPlaceService.controller;

import kr.ac.dankook.VettPlaceService.dto.response.ApiMessageResponse;
import kr.ac.dankook.VettPlaceService.dto.response.ApiResponse;
import kr.ac.dankook.VettPlaceService.dto.response.BookmarkResponse;
import kr.ac.dankook.VettPlaceService.entity.Passport;
import kr.ac.dankook.VettPlaceService.service.BookmarkService;
import kr.ac.dankook.VettPlaceService.util.PassportMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/places/bookmark")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/{placeId}")
    public ResponseEntity<ApiMessageResponse> addBookmark(@PassportMember Passport passport, @PathVariable Long placeId){
        bookmarkService.saveBookmark(placeId,passport.getKey());
        return ResponseEntity.status(201).body(new ApiMessageResponse(true,201,
                "즐겨찾기 등록에 성공하였습니다."));
    }

    @DeleteMapping("/{bookmarkId}")
    public ResponseEntity<ApiMessageResponse> deleteBookmark(
            @PassportMember Passport passport,
            @PathVariable Long bookmarkId){
        bookmarkService.deleteBookmark(passport.getKey(),bookmarkId);
        return ResponseEntity.status(200).body(new ApiMessageResponse(true,201,
                "즐겨찾기 해제에 성공하였습니다."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookmarkResponse>>> getBookmark(@PassportMember Passport passport){
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                bookmarkService.getBookmarkListByMemberId(passport.getKey())));
    }
}
