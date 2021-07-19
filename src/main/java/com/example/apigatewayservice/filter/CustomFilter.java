package com.example.apigatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j   //CustomFilter 사용시 AbstractGatewayFilterFactory 상속 받아서 사용한다.
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    public CustomFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        //Custom Pre Filter. JWT 토큰을 추출하여 권한을 확인 한다.
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest(); //서버 객체의 request 를 가져온다. reactive 비동기 방식의 통신중 filter 를 사용 할 경우 ServerHttpRequest 객체를 사용
            ServerHttpResponse response = exchange.getResponse(); //서버 객체의  response 를 가져온다. reactive 비동기 방식의 통신중 filter 를 사용 할 경우 ServerHttpResponse 객체를 사용

            log.info("Custom PRE filter : request id -> {}",request.getId());

            // Custom Post Filter.Spring5 에서 WebFlex 를 사용할 경우 기존의 ServletRequest 가 아닌 Mono 타입으로 반환 하여 준다.
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Custom POST filter : response id -> {}",response.getStatusCode());
            }));
        });
    }
    public static class Config{
        // put the configuration properties
    }
}
