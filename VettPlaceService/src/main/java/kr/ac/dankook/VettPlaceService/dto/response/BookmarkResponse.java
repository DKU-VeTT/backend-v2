package kr.ac.dankook.VettPlaceService.dto.response;

import kr.ac.dankook.VettPlaceService.entity.Bookmark;
import kr.ac.dankook.VettPlaceService.util.converter.PlaceEntityConverter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookmarkResponse {

    private Long id;
    private PlaceResponse placeResponse;

    public BookmarkResponse(Bookmark bookmark){
        this.id = bookmark.getId();
        this.placeResponse = PlaceEntityConverter.convertToPlaceResponse(bookmark.getPlace(),false);
    }
}
