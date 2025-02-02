package br.com.urbana.apigateway.controller;

import br.com.urbana.apigateway.core.inbound.WebhookInputPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class WebhookController {

    private static final String X_HUB_SIGNATURE_256 = "X-Hub-Signature-256";

    @Value("${whatsapp.verify_token}")
    private String webhookVerifyToken;

    @Value("${whatsapp.graph_api_token}")
    private String graphApiToken;

    private final WebhookInputPort webhookInputPort;

    @Autowired
    public WebhookController(WebhookInputPort webhookInputPort) {
        this.webhookInputPort = webhookInputPort;
    }

    @PostMapping("/webhook")
    public Mono<ResponseEntity<String>> webhook(@RequestBody String payloadString, @RequestHeader(X_HUB_SIGNATURE_256) String signature) {
        return Mono.fromRunnable(() -> webhookInputPort.handleWebhook(signature, payloadString))
            .thenReturn(ResponseEntity.ok().build());
    }

    @GetMapping("/webhook")
    public Mono<ResponseEntity<String>> verifyWebhook(
        @RequestParam(name = "hub.mode", required = false) String mode,
        @RequestParam(name = "hub.verify_token", required = false) String token,
        @RequestParam(name = "hub.challenge", required = false) String challenge) {

        if ("subscribe".equals(mode) && webhookVerifyToken.equals(token)) {
            log.info("Webhook verified successfully!");
            return Mono.just(ResponseEntity.ok(challenge));
        } else {
            log.error("Mode {} - Token {}", mode, token);
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
        }
    }
}