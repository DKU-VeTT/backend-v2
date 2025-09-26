package kr.ac.dankook.VettDiagnosisService.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Passport {

    private String key;
    private String name;
    private String userId;
    private String email;
    private String role;

}
