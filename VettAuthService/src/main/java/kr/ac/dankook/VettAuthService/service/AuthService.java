package kr.ac.dankook.VettAuthService.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import kr.ac.dankook.VettAuthService.config.principal.PrincipalDetails;
import kr.ac.dankook.VettAuthService.dto.request.LoginRequest;
import kr.ac.dankook.VettAuthService.dto.request.MailRequest;
import kr.ac.dankook.VettAuthService.dto.request.SignupRequest;
import kr.ac.dankook.VettAuthService.dto.response.TokenResponse;
import kr.ac.dankook.VettAuthService.entity.*;
import kr.ac.dankook.VettAuthService.error.ErrorCode;
import kr.ac.dankook.VettAuthService.error.exception.CustomException;
import kr.ac.dankook.VettAuthService.jwt.JwtTokenProvider;
import kr.ac.dankook.VettAuthService.repository.MemberRepository;
import kr.ac.dankook.VettAuthService.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthCacheService authCacheService;
    private final AuthMailService authMailService;


    private MailRequest generateMailRequest(String email, String randomCode) {
        String content = "해당 인증번호를 통해 인증을 완료해주세요. - " + randomCode;
        String title = "[VeTT] 인증번호 안내 이메일입니다.";
        return new MailRequest(email,title,content);
    }

    public void sendCertificateCode(Member member){
        String randomCode = RandomStringUtils.randomAlphanumeric(6);
        MailRequest mail = generateMailRequest(member.getEmail(),randomCode);
        authMailService.sendMail(mail);
        authCacheService.saveCertificateCode(member,randomCode);
    }

    public List<String> getUserIdByNameAndEmail(String name, String email){
        List<Member> members = memberRepository.findByNameAndEmail(name, email);
        return members.stream().map(Member::getUserId).toList();
    }

    public boolean isDuplicatedId(String userId){
        return memberRepository.existsByUserId(userId);
    }

    @Transactional
    public void signup(SignupRequest signupRequest) {
        if (memberRepository.existsByUserId(signupRequest.getUserId())){
            throw new CustomException(ErrorCode.DUPLICATE_ID);
        }
        Member newMember = Member.builder()
                .email(signupRequest.getEmail())
                .name(signupRequest.getName())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .userId(signupRequest.getUserId())
                .role(MemberRole.USER).build();
        memberRepository.save(newMember);
    }

    public TokenResponse login(LoginRequest loginRequest){

        try{
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(loginRequest.getUserId(),loginRequest.getPassword());
            Authentication authentication = authenticationManager.getObject()
                    .authenticate(authenticationToken);
            return createTokenAndSaveInCache(authentication,loginRequest.getUserId());
        }catch (InternalAuthenticationServiceException | BadCredentialsException e){
            throw new CustomException(ErrorCode.BAD_CREDENTIAL);
        }
    }

    public TokenResponse reissueToken(String refreshToken){

        Authentication authentication;
        try{
            authentication = jwtTokenProvider.validateToken(refreshToken.trim());
        }catch (JWTVerificationException e){
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        String userId = principalDetails.getUsername();

        Optional<String> existRefreshToken = authCacheService.getRefreshToken(userId);
        if (existRefreshToken.isEmpty() || !existRefreshToken.get().equals(refreshToken)){
            throw new CustomException(ErrorCode.BAD_CREDENTIAL);
        }
        return createTokenAndSaveInCache(authentication,userId);
    }


    public void logout(String userId){
        authCacheService.deleteKey(userId);
        SecurityContextHolder.clearContext();
    }


    private TokenResponse createTokenAndSaveInCache(Authentication authentication,String userId){
        TokenResponse tokens = new TokenResponse(
                jwtTokenProvider.generateToken(authentication, TokenType.ACCESS_TOKEN),
                jwtTokenProvider.generateToken(authentication,TokenType.REFRESH_TOKEN)
        );
        authCacheService.saveRefreshToken(userId, tokens.getRefreshToken());
        return tokens;
    }
}
