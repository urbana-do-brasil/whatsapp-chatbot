package br.com.urbana.apigateway.controller;

import br.com.urbana.apigateway.core.inbound.WebhookInputPort;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
public class WebhookController {

    private final WebhookInputPort webhookInputPort;

    @PostMapping("/webhook")
    public Mono<ResponseEntity<String>> webhook(@RequestBody String payload) {
        webhookInputPort.handleWebhook(payload);
        return Mono.just(ResponseEntity.ok("Webhook recebido!"));
    }
}