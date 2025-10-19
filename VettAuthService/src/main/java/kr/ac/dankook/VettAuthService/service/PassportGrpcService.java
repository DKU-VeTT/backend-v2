package kr.ac.dankook.VettAuthService.service;

import io.grpc.stub.StreamObserver;
import kr.ac.dankook.Passport;
import kr.ac.dankook.PassportServiceGrpc;
import kr.ac.dankook.VettAuthService.entity.Member;
import kr.ac.dankook.VettAuthService.error.ErrorCode;
import kr.ac.dankook.VettAuthService.error.exception.CustomException;
import kr.ac.dankook.VettAuthService.log.LogMessage;
import kr.ac.dankook.VettAuthService.repository.MemberRepository;
import kr.ac.dankook.VettAuthService.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;


@GrpcService
@RequiredArgsConstructor
@Slf4j
public class PassportGrpcService extends PassportServiceGrpc.PassportServiceImplBase {

    private final MemberRepository memberRepository;

    @Override
    public void getPassport(Passport.PassportRequest request, StreamObserver<Passport.PassportResponse> responseObserver) {

        String[] classNames = Thread.currentThread().getStackTrace()[1].getClassName().split("\\.");
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String className = classNames[classNames.length - 1];

        String key = request.getKey();
        log.info("[{}, class={}, method={}, requestKey={}]",
                LogMessage.CLOUD_GATEWAY_REQUEST, className, methodName, key);

        Member member = memberRepository.findById(EncryptionUtil.decrypt(key))
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND, className, methodName,"USER KEY : " + key));

        Passport.PassportResponse response = Passport.PassportResponse.newBuilder()
                .setKey(key)
                .setEmail(member.getEmail())
                .setUserId(member.getUserId())
                .setRole(member.getRole().name())
                .setName(member.getName()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
