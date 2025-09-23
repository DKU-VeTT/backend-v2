package kr.ac.dankook.VettPlaceService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {
    private final boolean success;
    private final int statusCode;
    private final T Data;
}
