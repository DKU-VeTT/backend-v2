package kr.ac.dankook.VettAuthService.service;

import kr.ac.dankook.VettAuthService.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import static kr.ac.dankook.VettAuthService.jwt.JwtTokenProvider.REFRESH_TOKEN_EXPIRE_TIME;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthCacheService {

    // 인증메일 15분동안 유효
    private static final long CERTIFICATE_EXPIRE_TIME = 1000 * 60 * 15;
    private final RedisTemplate<String,String> redisTemplate;

    public String generateKey(Member member){
        return member.getEmail()+"_"+member.getUserId();
    }

    @SuppressWarnings({"ConstantConditions"})
    public void saveRefreshToken(String userId,String refreshToken){
        String existToken = redisTemplate.opsForValue().get(userId);
        if (existToken != null && !existToken.isBlank()){
            deleteKey(userId);
        }
        redisTemplate.opsForValue().set(userId, refreshToken,REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    @SuppressWarnings({"ConstantConditions"})
    public Optional<String> getRefreshToken(String userId){
        String existToken = redisTemplate.opsForValue().get(userId);
        return Optional.ofNullable(existToken);
    }

    @SuppressWarnings({"ConstantConditions"})
    public void saveCertificateCode(Member member,String code){
        String key = generateKey(member);
        String cacheResult = redisTemplate.opsForValue().get(key);
        if (cacheResult != null && !cacheResult.isBlank()){
            deleteKey(key);
        }
        redisTemplate.opsForValue().set(key,code,CERTIFICATE_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    @SuppressWarnings({"ConstantConditions"})
    public boolean isVerifyCode(Member member, String code){
        String key = generateKey(member);
        String cacheResult = redisTemplate.opsForValue().get(key);
        return cacheResult != null && cacheResult.equals(code);
    }

    public void deleteKey(String key){
        redisTemplate.delete(key);
    }

}
