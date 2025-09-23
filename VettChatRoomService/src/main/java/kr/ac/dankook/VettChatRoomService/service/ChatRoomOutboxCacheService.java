package kr.ac.dankook.VettChatRoomService.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomOutboxCacheService {

    private final RedisTemplate<String,String> redisTemplate;

    @SuppressWarnings({"ConstantConditions"})
    public boolean checkIsAlreadyPublish(String outboxId){
        String val = redisTemplate.opsForValue().get(outboxId);
        return val != null;
    }

    public void setOutboxId(String outboxId, long ttl){
        redisTemplate.opsForValue().set(outboxId, "PUB",ttl, TimeUnit.SECONDS);
    }
    public void deleteOutboxId(String outboxId){
        redisTemplate.delete(outboxId);
    }

    public void deleteOutboxId(Set<String> outboxIds) {
        redisTemplate.delete(outboxIds);
    }

}
