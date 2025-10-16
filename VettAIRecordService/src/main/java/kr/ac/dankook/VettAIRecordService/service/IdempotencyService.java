package kr.ac.dankook.VettAIRecordService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettAIRecordService.error.ErrorCode;
import kr.ac.dankook.VettAIRecordService.error.exception.CustomException;
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
    // idem key가 없는 경우 400
    @SuppressWarnings({"ConstantConditions"})
    public <T> T execute(String key, Supplier<T> action,Class<T> tClass){

        // Idem Key가 없는 경우
        if (key == null || key.isBlank()){
            throw new CustomException(ErrorCode.IDEMPOTENCY_KEY_REQUIRED);
        }
        String stateKey = "idem:" + key  + ":state";
        String respKey = "idem:" + key + ":resp";

        // 1) state key를 통해 lock 획득
        boolean locked = redisTemplate.opsForValue().setIfAbsent(stateKey,"processing", PROCESSING_TTL, TimeUnit.MILLISECONDS);
        if (!locked){
            // 2) 이미 누군가 처리 중 / 완료 -> 응답 캐시가 있으면 즉시 반환
            String cached = redisTemplate.opsForValue().get(respKey);
            if (cached != null){
                try{
                    return objectMapper.readValue(cached, tClass);
                }catch (JsonProcessingException e){
                    throw new CustomException(ErrorCode.JSON_PROCESSING_ERROR);
                }
            }
            // lock을 얻지 못하고 ( 충돌 상황 ) & cache 값도 없는 상황 or 다른 요청을 통해 처리중인 상황
            throw new CustomException(ErrorCode.IDEMPOTENCY_IN_PROGRESS_CONFLICT);
        }

        try{
            T result = action.get();
            String jsonResult = objectMapper.writeValueAsString(result);
            // state key와 결과 값을 저장
            redisTemplate.opsForValue().set(respKey,jsonResult, RESULT_TTL, TimeUnit.MILLISECONDS);
            redisTemplate.opsForValue().set(stateKey,"done", RESULT_TTL,TimeUnit.MILLISECONDS);
            return result;
        }catch (JsonProcessingException e){
            redisTemplate.delete(stateKey);
            throw new CustomException(ErrorCode.JSON_PROCESSING_ERROR);
        }catch (Throwable t){
            redisTemplate.delete(stateKey);
            throw t;
        }
    }
}
