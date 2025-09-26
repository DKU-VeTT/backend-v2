package kr.ac.dankook.VettDiagnosisService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private final boolean success;
    private final int statusCode;
    private final T data;
}
