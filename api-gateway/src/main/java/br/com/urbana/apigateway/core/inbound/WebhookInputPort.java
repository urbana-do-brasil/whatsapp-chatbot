package br.com.urbana.apigateway.core.inbound;

import reactor.core.publisher.Mono;

public interface WebhookInputPort {
    Mono<Void> handleWebhook(String signature, String payloadString);
}