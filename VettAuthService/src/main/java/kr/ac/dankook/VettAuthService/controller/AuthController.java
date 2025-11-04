package kr.ac.dankook.VettAuthService.controller;

import jakarta.validation.Valid;
import kr.ac.dankook.VettAuthService.dto.request.LoginRequest;
import kr.ac.dankook.VettAuthService.dto.request.PasswordChangeRequest;
import kr.ac.dankook.VettAuthService.dto.request.SignupRequest;
import kr.ac.dankook.VettAuthService.dto.response.ApiMessageResponse;
import kr.ac.dankook.VettAuthService.dto.response.ApiResponse;
import kr.ac.dankook.VettAuthService.dto.response.TokenResponse;
import kr.ac.dankook.VettAuthService.entity.Member;
import kr.ac.dankook.VettAuthService.service.AuthCacheService;
import kr.ac.dankook.VettAuthService.service.AuthService;
import kr.ac.dankook.VettAuthService.service.IdempotencyService;
import kr.ac.dankook.VettAuthService.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;
    private final AuthCacheService authCacheService;
    private final IdempotencyService idempotencyService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiMessageResponse> signup(
            @RequestBody @Valid SignupRequest signupRequest,
            @RequestHeader("Idempotency-Key") String key
    ) {
        idempotencyService.execute(key, () -> {
            authService.signup(signupRequest);
            return null;
        });
        return ResponseEntity.status(201)
                .body(new ApiMessageResponse(true,201,"회원가입을 완료하였습니다."));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @RequestBody @Valid LoginRequest loginRequest,
            @RequestHeader("Idempotency-Key") String key
    ){
        TokenResponse res = idempotencyService.execute(
                key,
                () ->   authService.login(loginRequest),
                TokenResponse.class
        );
        return ResponseEntity.status(200)
                .body(new ApiResponse<>(true,200,
                        res));
    }

    // 아이디 중복 체크
    @GetMapping("/duplicate")
    public ResponseEntity<ApiResponse<Boolean>> checkDuplicatedId(
            @RequestParam("userId") String userId){
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                authService.isDuplicatedId(userId)));
    }

    // 아이디 찾기 - 이름,이메일 -> 아이디 리스트 ( 아이디만 중복 X )
    @GetMapping("/find-id")
    public ResponseEntity<ApiResponse<List<String>>> findId(
            @RequestParam("name") String name,
            @RequestParam("email") String email
    ){
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                authService.getUserIdByNameAndEmail(name,email)));
    }

    // 비밀번호 찾기를 위한 인증번호 전송
    @PostMapping("/find-password/certificate")
    public ResponseEntity<ApiResponse<String>> sendCertificateCode(
            @RequestParam("userId") String userId,
            @RequestHeader("Idempotency-Key") String key
    ){
        Member member = memberService.getCurrentMember(userId);
        String res = idempotencyService.execute(
                key,
                () ->   authService.sendCertificateCode(member),
                String.class
        );
        return ResponseEntity.status(201).body(new ApiResponse<>(true,200,
                res));
    }

    // 인증번호 검사
    @GetMapping("/find-password/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifyCertificateCode(
            @RequestParam("userId") String userId,
            @RequestParam("code") String code
    ){
        Member member = memberService.getCurrentMember(userId);
        return ResponseEntity.status(200).body(new ApiResponse<>(true,200,
                authCacheService.isVerifyCode(member,code)));
    }

    // 인증 완료 후 비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity<ApiMessageResponse> changePassword(
            @RequestParam("userId") String userId,
            @RequestBody @Valid PasswordChangeRequest request
    ){
        Member member = memberService.getCurrentMember(userId);
        memberService.updatePassword(member,request.getNewPassword());
        authCacheService.deleteKey(authCacheService.generateKey(member));
        return ResponseEntity.status(200).body(new ApiMessageResponse(true,200,
                "비밀번호 변경을 완료하였습니다."));
    }

    // 토큰 갱신
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(
            @RequestParam("refreshToken") String refreshToken,
            @RequestHeader("Idempotency-Key") String key
    ){
        TokenResponse res = idempotencyService.execute(
                key,
                () ->   authService.reissueToken(refreshToken),
                TokenResponse.class
        );
        return ResponseEntity.status(201).body(new ApiResponse<>(true,200,
                res));
    }
}
