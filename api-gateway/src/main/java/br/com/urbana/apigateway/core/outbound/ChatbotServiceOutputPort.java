package br.com.urbana.apigateway.core.outbound;

import br.com.urbana.apigateway.model.WhatsAppMessage;

public interface ChatbotServiceOutputPort {
    void sendMessage(WhatsAppMessage message);
}