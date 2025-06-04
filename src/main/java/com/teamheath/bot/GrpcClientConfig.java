package com.teamheath.bot;

import com.teamhealth.grpc.ScoreServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Bean
    public ManagedChannel grpcChannel() {
        return ManagedChannelBuilder // again here is where you need the localhost
                .forAddress("cpp-microservice", 50051)
                .usePlaintext()
                .build();
    }

    @Bean
    public ScoreServiceGrpc.ScoreServiceBlockingStub scoreServiceStub(ManagedChannel grpcChannel) {
        return ScoreServiceGrpc.newBlockingStub(grpcChannel);
    }
}