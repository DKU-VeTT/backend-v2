package kr.ac.dankook.VettAuthService.log;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LogError {
    private String uri;
    private String className;
    private String methodName;
    private String userKey;
}
