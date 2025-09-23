package kr.ac.dankook.VettAuthService.entity;

import jakarta.persistence.*;
import kr.ac.dankook.VettAuthService.util.EncryptionUtil;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Builder
    public Member(String userId, String password, String email,
                  String name, MemberRole role) {
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.name = name;
        this.role = role;
        this.status = MemberStatus.ACTIVE;
    }

    public void updatePassword(String newPassword){
        this.password = newPassword;
    }

    public void updateMemberInfo(String name,String email){
        this.name = name;
        this.email = email;
    }

    public void convertToDeletedMember(){
        this.status = MemberStatus.WITHDRAWN;
        this.password = "";
        this.email = "";
        this.name = "";
        this.userId = EncryptionUtil.encrypt(this.getId());
    }
}
