package kr.ac.dankook.VettAuthService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TokenResponse {

    private String accessToken;
    private String refreshToken;
}
