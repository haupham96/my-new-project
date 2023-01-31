package com.example.apigateway.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

@Slf4j
public class ReactiveWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        try {
            /* invalidate all session -> stateless sesssion */
            exchange.getSession().toFuture().get().invalidate();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return chain.filter(exchange);
    }
}
