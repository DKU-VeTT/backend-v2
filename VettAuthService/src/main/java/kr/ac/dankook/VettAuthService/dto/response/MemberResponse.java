package kr.ac.dankook.VettAuthService.dto.response;

import kr.ac.dankook.VettAuthService.entity.Member;
import kr.ac.dankook.VettAuthService.util.EncryptionUtil;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class MemberResponse {

    private String id;
    private String name;
    private String userId;
    private String email;
    private LocalDateTime createTime;
    private String role;

    public MemberResponse(Member member) {
        this.id = EncryptionUtil.encrypt(member.getId());
        this.name = member.getName();
        this.userId = member.getUserId();
        this.email = member.getEmail();
        this.createTime = LocalDateTime.now();
        this.role = member.getRole().name();
    }
}
