package br.com.urbana.apigateway.core.inbound;

import org.springframework.http.server.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface WebhookInputPort {
    Mono<Void> handleWebhook(ServerHttpRequest request, Map<String, Object> payload);
}