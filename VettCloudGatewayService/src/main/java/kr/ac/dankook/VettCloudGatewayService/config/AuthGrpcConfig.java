package kr.ac.dankook.VettCloudGatewayService.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import kr.ac.dankook.Passport;
import kr.ac.dankook.PassportServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthGrpcConfig {

    private final PassportServiceGrpc.PassportServiceBlockingStub stub;

    public AuthGrpcConfig( @Value("${app.domain.auth-service.grpc-port}") int port,
                           @Value("${app.domain.auth-service.grpc-host}") String grpcHost) {

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(grpcHost, port)
                .usePlaintext()
                .build();
        stub = PassportServiceGrpc.newBlockingStub(channel);
    }

    public Passport.PassportResponse getPassportInfo(String userKey) {

        Passport.PassportRequest request = Passport.PassportRequest.newBuilder()
                .setKey(userKey)
                .build();
        return stub.getPassport(request);
    }
}
