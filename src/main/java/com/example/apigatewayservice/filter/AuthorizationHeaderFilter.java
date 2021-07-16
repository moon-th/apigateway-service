package com.example.apigatewayservice.filter;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    Environment env;

    public AuthorizationHeaderFilter(Environment env){
        super(Config.class);
        this.env = env;
    }

    public static class Config{

    }


    //login -> token -> users (with token) -> header(include token)
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }
            //header 에서 권한 관련 jwt 토큰을 가지고 온다. 배열 형태로 넘어오기 때문에 0번쨰 값을 추출
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            //token 에서 Bearer 를 제외한 나머지 부분을 사용 한다.
            String jwt = authorizationHeader.replace("Bearer", "");

            if (!isJwtValid(jwt)) {
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        });
    }

    /**
     * Jwt token 검증 메서드
     * @param jwt
     * @return
     */
    private boolean isJwtValid(String jwt) {
        boolean returnValue = true;

        String subject = null;

        try {
            // 생성된 token 을 지정한 시크릿 키로 파싱한 다음 그 안에서 Subject 만 추출하여 문자열로 사용 한다.
            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJws(jwt).getBody()
                    .getSubject();
        }catch(Exception e){
            returnValue = false;
        }
        if(subject == null || subject.isEmpty()){
            returnValue = false;
        }
        return returnValue;
    }

    /**
     * Mono, Flux -> spring WebFlux
     * WebFlux 에서 데이터를 처리하는 두가지 단위중 하나가 Mono 이다. 단일값 처리 시 사용
     * 단일값이 아닐시 Flux 사용
     *
     * @param exchange
     * @param error
     * @param httpStatus
     * @return
     */
    private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus httpStatus) {
        //spring WebFlux 에서는 servlet 개념을 사용하지 않는다.
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(error);
        return response.setComplete();
    }

}
