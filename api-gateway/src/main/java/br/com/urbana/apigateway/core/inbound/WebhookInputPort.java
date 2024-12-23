package br.com.urbana.apigateway.core.inbound;

import java.util.Map;

public interface WebhookInputPort {
    void handleWebhook(Map<String, Object> payload);
}