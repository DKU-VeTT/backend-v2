package kr.ac.dankook.VettAuthService.service;

import io.grpc.stub.StreamObserver;
import kr.ac.dankook.MemberEvent;
import kr.ac.dankook.MemberEventServiceGrpc;
import kr.ac.dankook.VettAuthService.entity.Member;
import kr.ac.dankook.VettAuthService.repository.MemberRepository;
import kr.ac.dankook.VettAuthService.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;
import java.util.Optional;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class MemberGrpcService extends MemberEventServiceGrpc.MemberEventServiceImplBase {

    private final MemberRepository memberRepository;

    @Override
    public void getMemberInfo(MemberEvent.MemberEventRequest request, StreamObserver<MemberEvent.MemberEventResponse> responseObserver) {
        log.info("[gRPC Request] Received Member Event Request with ID : {}", request.getId());
        Optional<Member> member = memberRepository.findById(EncryptionUtil.decrypt(request.getId()));
        MemberEvent.MemberEventResponse response = null;
        if (member.isPresent()) {
            Member memberInfo = member.get();
            response = MemberEvent.MemberEventResponse.newBuilder()
                    .setId(request.getId())
                    .setUserId(memberInfo.getUserId())
                    .setEmail(memberInfo.getEmail())
                    .setName(memberInfo.getName())
                    .setRole(memberInfo.getRole().toString())
                    .build();
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
