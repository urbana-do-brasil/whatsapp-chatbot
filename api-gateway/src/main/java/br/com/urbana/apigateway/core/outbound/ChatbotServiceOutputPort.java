package br.com.urbana.apigateway.core.outbound;

public interface ChatbotServiceOutputPort {
    void sendMessage(String message);
}