package com.skillsphere.apigateway.config;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.isTokenValid(token)) {
                Claims claims = jwtUtil.extractAllClaims(token);
                String username = claims.getSubject();
                Object roles = claims.get("roles");
                // Forward username and roles to downstream services
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Name", username)
                        .header("X-User-Roles", roles != null ? roles.toString() : "")
                        .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            } else {
                return this.unauthorized(exchange.getResponse());
            }
        }
        // For endpoints that require auth, block if no token
        if (requiresAuth(request.getPath().toString())) {
            return this.unauthorized(exchange.getResponse());
        }
        return chain.filter(exchange);
    }

    private boolean requiresAuth(String path) {
        // Allowlist public endpoints
        return !(path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register")
                || path.startsWith("/actuator"));
    }

    private Mono<Void> unauthorized(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }
}