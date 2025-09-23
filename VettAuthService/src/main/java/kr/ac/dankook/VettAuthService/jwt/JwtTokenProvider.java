package kr.ac.dankook.VettAuthService.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import kr.ac.dankook.VettAuthService.config.principal.PrincipalDetails;
import kr.ac.dankook.VettAuthService.entity.Member;
import kr.ac.dankook.VettAuthService.entity.TokenType;
import kr.ac.dankook.VettAuthService.repository.MemberRepository;
import kr.ac.dankook.VettAuthService.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${app.secret.jwt}")
    private String secretKey;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // 30 minutes
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 30; // 30 day
    private final MemberRepository memberRepository;

    public String generateToken(Authentication authentication,TokenType tokenType) {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Member member = principalDetails.getMember();

        long now = (new Date()).getTime();

        Date expired = tokenType == TokenType.ACCESS_TOKEN ?
                new Date(now + ACCESS_TOKEN_EXPIRE_TIME) :
                new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        return JWT.create()
                .withSubject(principalDetails.getUsername())
                .withExpiresAt(expired)
                .withClaim("key", EncryptionUtil.encrypt(member.getId()))
                .withClaim("role",member.getRole().getTitle())
                .sign(Algorithm.HMAC512(secretKey));
    }

    public Authentication validateToken(String jwtToken){
        String key = getUserKeyFromToken(jwtToken);
        Long decryptId = EncryptionUtil.decrypt(key);
        Optional<Member> entity = memberRepository.findById(decryptId);
        if (entity.isPresent()) {
            PrincipalDetails principalDetails = new PrincipalDetails(entity.get());
            return new UsernamePasswordAuthenticationToken(
                    principalDetails, jwtToken, principalDetails.getAuthorities());
        }
        return null;
    }

    private String getUserKeyFromToken(String jwtToken){
        return JWT.require(Algorithm.HMAC512(secretKey))
                .build().verify(jwtToken).getClaim("key")
                .asString();
    }
}
