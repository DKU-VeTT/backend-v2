package kr.ac.dankook.VettChatService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettChatService.error.ErrorCode;
import kr.ac.dankook.VettChatService.error.exception.CustomException;
import kr.ac.dankook.VettChatService.log.LogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {

    private final RedisTemplate<String,String> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final long PROCESSING_TTL = 1000 * 60 * 2;
    private static final long RESULT_TTL = 1000 * 60 * 15;

    @SuppressWarnings({"ConstantConditions"})
    public <T> T execute(String key, Supplier<T> action,Class<T> tClass){

        if (key == null || key.isBlank()){
            throw new CustomException(ErrorCode.IDEMPOTENCY_KEY_REQUIRED);
        }
        String stateKey = "idem:" + key  + ":state";
        String respKey = "idem:" + key + ":resp";

        boolean locked = redisTemplate.opsForValue().setIfAbsent(stateKey,"processing", PROCESSING_TTL, TimeUnit.MILLISECONDS);
        if (!locked){
            String cached = redisTemplate.opsForValue().get(respKey);
            if (cached != null){
                try{
                    return objectMapper.readValue(cached, tClass);
                }catch (JsonProcessingException e){
                    log.error("{}, CLASS={}, METHOD={}, KEY={}, ERROR={}",
                            LogMessage.JSON_PROCESSING_ERROR, "IdempotencyService", "execute", key,
                            e.getMessage());
                    throw new CustomException(ErrorCode.JSON_PROCESSING_ERROR);
                }
            }
            throw new CustomException(ErrorCode.IDEMPOTENCY_IN_PROGRESS_CONFLICT);
        }

        try{
            T result = action.get();
            String jsonResult = objectMapper.writeValueAsString(result);
            redisTemplate.opsForValue().set(respKey,jsonResult, RESULT_TTL, TimeUnit.MILLISECONDS);
            redisTemplate.opsForValue().set(stateKey,"done", RESULT_TTL,TimeUnit.MILLISECONDS);
            return result;
        }catch (JsonProcessingException e){
            log.error("{}, CLASS={}, METHOD={}, ERROR={}",
                    LogMessage.JSON_PROCESSING_ERROR, "IdempotencyService", "execute",
                    e.getMessage());
            redisTemplate.delete(stateKey);
            throw new CustomException(ErrorCode.JSON_PROCESSING_ERROR);
        }catch (Throwable t){
            redisTemplate.delete(stateKey);
            throw t;
        }
    }

    public void execute(String key, Supplier<Void> action) {

        if (key == null || key.isBlank()) {
            throw new CustomException(ErrorCode.IDEMPOTENCY_KEY_REQUIRED);
        }
        String stateKey = "idem:" + key + ":state";
        boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(stateKey, "processing", PROCESSING_TTL, TimeUnit.MILLISECONDS);
        if (!locked) {
            String state = redisTemplate.opsForValue().get(stateKey);
            if ("done".equals(state)) return;
            throw new CustomException(ErrorCode.IDEMPOTENCY_IN_PROGRESS_CONFLICT);
        }
        try {
            action.get();
            redisTemplate.opsForValue().set(stateKey, "done", RESULT_TTL, TimeUnit.MILLISECONDS);
        } catch (Throwable t) {
            redisTemplate.delete(stateKey);
            throw t;
        }
    }
}
