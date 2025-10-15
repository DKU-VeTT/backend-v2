package kr.ac.dankook.VettCloudGatewayService.config;

import brave.Tracing;
import brave.grpc.GrpcTracing;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import kr.ac.dankook.PassportServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AuthGrpcConfig {

    private final PassportServiceGrpc.PassportServiceBlockingStub stub;

    public AuthGrpcConfig(@Value("${app.domain.auth-service.grpc-port}") int port,
                          @Value("${app.domain.auth-service.grpc-host}") String grpcHost,
                          Tracing tracing) {

        GrpcTracing grpcTracing = GrpcTracing.create(tracing);
        ClientInterceptor clientInterceptor = grpcTracing.newClientInterceptor();

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(grpcHost, port)
                .usePlaintext()
                .intercept(clientInterceptor)
                .build();

        stub = PassportServiceGrpc.newBlockingStub(channel);
    }

    @Bean
    public PassportServiceGrpc.PassportServiceBlockingStub passportServiceStub(){
        return stub;
    }
}
