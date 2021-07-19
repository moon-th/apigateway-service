package com.example.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        //Custom Pre Filter
//        return ((exchange, chain) -> {
//            ServerHttpRequest request = exchange.getRequest(); //서버 객체의 request 를 가져오자
//            ServerHttpResponse response = exchange.getResponse(); //서버 객체의  response 를 가져오자
//
//            log.info("Global Filter baseMessage {}",config.getBaseMessage());
//
//            if(config.isPreLogger()){
//                log.info("Global Filter Start : request id{}",request.getId());
//            }
//
//            // Custom Post Filter
//            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
//
//                if(config.isPostLogger()){
//                    log.info("Global Filter End : response code -> {}",response.getStatusCode());
//                }
//            }));
//        });
        GatewayFilter filter = new OrderedGatewayFilter((exchange, chain) ->{
            ServerHttpRequest request = exchange.getRequest(); //서버 객체의 request 를 가져오자
            ServerHttpResponse response = exchange.getResponse(); //서버 객체의  response 를 가져오자

            log.info("Logging Filter baseMessage {}",config.getBaseMessage());

            if(config.isPreLogger()){
                log.info("Logging  PRE Filter : request id{}",request.getId());
            }

            // Custom Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {

                if(config.isPostLogger()){
                    log.info("Logging POST Filter : response code -> {}",response.getStatusCode());
                }
            }));
            //Ordered.XXX -> 필터의 실행 순서를 정할 수 있다.
        }, Ordered.LOWEST_PRECEDENCE);
        return filter;

    }

    @Data
    public static class Config{
        // put the configuration properties
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;

    }

}
