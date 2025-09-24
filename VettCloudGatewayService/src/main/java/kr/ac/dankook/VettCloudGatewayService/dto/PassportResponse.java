package kr.ac.dankook.VettCloudGatewayService.dto;

import kr.ac.dankook.Passport;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassportResponse {

    private String key;
    private String name;
    private String email;
    private String userId;
    private String role;

    public PassportResponse(Passport.PassportResponse response){
        this.key = response.getKey();
        this.name = response.getName();
        this.email = response.getEmail();
        this.userId = response.getUserId();
        this.role = response.getRole();
    }
}
