package com.example.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        //Custom Pre Filter
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest(); //서버 객체의 request 를 가져오자
            ServerHttpResponse response = exchange.getResponse(); //서버 객체의  response 를 가져오자

            log.info("Global Filter baseMessage {}",config.getBaseMessage());

            if(config.isPreLogger()){
                log.info("Global Filter Start : request id{}",request.getId());
            }

            // Custom Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {

                if(config.isPostLogger()){
                    log.info("Global Filter End : response code -> {}",response.getStatusCode());
                }
            }));
        });
    }

    @Data
    public static class Config{
        // put the configuration properties
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }

}
