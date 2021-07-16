package com.example.apigatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApigatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApigatewayServiceApplication.class, args);
    }

    /**
     * 클라리언트가 요청한 trace 데이터를 메모리에 담기고 우리가 필요할 때 end 포인트로 사용
     * @return
     */
    @Bean
    public HttpTraceRepository httpTraceRepository() {
        return new InMemoryHttpTraceRepository();
    }
}
