package kr.ac.dankook.VettAuthService.config;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import kr.ac.dankook.VettAuthService.service.MemberGrpcService;
import kr.ac.dankook.VettAuthService.service.PassportGrpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class GrpcConfig {

    @Value("${spring.grpc.server.port}")
    private int port;

    @Bean
    public Server grpcServer(MemberGrpcService memberGrpcService, PassportGrpcService passportGrpcService) throws IOException {

        Server server = ServerBuilder
                .forPort(port)
                .addService(memberGrpcService)
                .addService(passportGrpcService)
                .build();

        server.start();
        log.info("[gRPC Server] gRPC Server started on port {}",port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("[gRPC Server] Shutting down gRPC server.");
            server.shutdown();
        }));

        return server;
    }
}
