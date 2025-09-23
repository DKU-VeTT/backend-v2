package kr.ac.dankook.VettAuthService.dto.response;

import kr.ac.dankook.VettAuthService.entity.Member;
import kr.ac.dankook.VettAuthService.util.EncryptionUtil;
import lombok.*;

@Getter
@Setter
public class PassportResponse {

    private String key;
    private String name;
    private String userId;
    private String email;
    private String role;

    @Builder
    public PassportResponse(Member member) {
        this.key = EncryptionUtil.encrypt(member.getId());
        this.name = member.getName();
        this.userId = member.getUserId();
        this.email = member.getEmail();
        this.role = member.getRole().name();
    }
}
