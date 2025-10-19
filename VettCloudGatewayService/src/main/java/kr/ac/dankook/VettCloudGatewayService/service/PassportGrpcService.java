package kr.ac.dankook.VettCloudGatewayService.service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import kr.ac.dankook.Passport;
import kr.ac.dankook.PassportServiceGrpc;
import kr.ac.dankook.VettCloudGatewayService.error.CustomException;
import kr.ac.dankook.VettCloudGatewayService.error.ErrorCode;
import kr.ac.dankook.VettCloudGatewayService.log.LogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;
import java.util.concurrent.TimeUnit;

@GrpcService
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
                .withDeadlineAfter(400,TimeUnit.MILLISECONDS)
                .getPassport(request);
    }

    public Passport.PassportResponse getPassportFallback(String userKey, Throwable t) {

        String[] classNames = Thread.currentThread().getStackTrace()[1].getClassName().split("\\.");
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String className = classNames[classNames.length - 1];
        throw new CustomException(ErrorCode.PASSPORT_ERROR,className,methodName,
                "KEY : " + userKey + " cause : " + t.getMessage());
    }
}
