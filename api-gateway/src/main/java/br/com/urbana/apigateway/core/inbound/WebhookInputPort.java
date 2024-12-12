package br.com.urbana.apigateway.core.inbound;

public interface WebhookInputPort {
    void handleWebhook(String payload);
}