package kr.ac.dankook.VettCloudGatewayService.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import kr.ac.dankook.Passport;
import kr.ac.dankook.PassportServiceGrpc;
import kr.ac.dankook.VettCloudGatewayService.error.CustomException;
import kr.ac.dankook.VettCloudGatewayService.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassportGrpcService {

    private final PassportServiceGrpc.PassportServiceBlockingStub passportServiceStub;

    @CircuitBreaker(name = "passport", fallbackMethod = "getPassportFallback")
    public Passport.PassportResponse getPassportInfo(String userKey) {
        Passport.PassportRequest request = Passport.PassportRequest.newBuilder()
                .setKey(userKey)
                .build();
        return passportServiceStub
                .withDeadlineAfter(500,TimeUnit.MILLISECONDS)
                .getPassport(request);
    }

    public Passport.PassportResponse getPassportFallback(String userKey, Throwable t) {
        log.error("Fallback called for passport userKey: {} due to: {}", userKey, t.getMessage());
        throw new CustomException(ErrorCode.PASSPORT_ERROR);
    }
}
