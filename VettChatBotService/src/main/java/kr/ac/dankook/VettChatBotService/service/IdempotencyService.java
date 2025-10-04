package kr.ac.dankook.VettChatBotService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettChatBotService.error.ErrorCode;
import kr.ac.dankook.VettChatBotService.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService{

    private final RedisTemplate<String,String> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final long PROCESSING_TTL = 1000 * 60 * 2; // 2 minutes
    private static final long RESULT_TTL = 1000 * 60 * 15; // 15 minutes

    // 동일 idem Key로 두 번 호출 -> 캐시 응답으로 반환 ( 중복 요청 제거 및 멱등성 보장 )
    // 같은 idem Key지만 다른 bodyHash -> 422
    // idem key가 없는 경우 400
    @SuppressWarnings({"ConstantConditions"})
    public <T> T execute(String key, String bodyHash, Supplier<T> action,Class<T> tClass){

        if (key == null || key.isBlank()){
            throw new CustomException(ErrorCode.IDEMPOTENCY_KEY_REQUIRED);
        }
        String stateKey = "idem:" + key  + ":state";
        String hashKey = "idem:" + key  + ":hash";
        String respKey = "idem:" + key + ":resp";

        String prevHash = redisTemplate.opsForValue().get(hashKey);

        if (prevHash != null && !prevHash.equals(bodyHash)){
            throw new CustomException(ErrorCode.IDEMPOTENCY_BODY_MISMATCH);
        }

        boolean locked = redisTemplate.opsForValue().setIfAbsent(stateKey,"processing", PROCESSING_TTL, TimeUnit.MILLISECONDS);
        if (!locked){
            String cached = redisTemplate.opsForValue().get(respKey);
            if (cached != null){
                try{
                    return objectMapper.readValue(cached, tClass);
                }catch (JsonProcessingException e){
                    throw new CustomException(ErrorCode.IDEMPOTENCY_IN_PROGRESS_CONFLICT);
                }
            }
            throw new CustomException(ErrorCode.IDEMPOTENCY_IN_PROGRESS_CONFLICT);
        }
        try{
            if (prevHash == null){
                redisTemplate.opsForValue().set(hashKey,bodyHash,RESULT_TTL,TimeUnit.MILLISECONDS);
            }
            T result = action.get();
            String jsonResult = objectMapper.writeValueAsString(result);
            redisTemplate.opsForValue().set(respKey,jsonResult,RESULT_TTL, TimeUnit.MILLISECONDS);
            redisTemplate.opsForValue().set(stateKey,"done",RESULT_TTL,TimeUnit.MILLISECONDS);
            return result;
        }catch (JsonProcessingException e){
            redisTemplate.delete(stateKey);
            throw new CustomException(ErrorCode.JSON_PROCESSING_ERROR);
        }
    }
}
