package br.com.urbana.apigateway.gateway;

import br.com.urbana.apigateway.core.outbound.ChatbotServiceOutputPort;
import org.springframework.stereotype.Component;

@Component
public class ChatbotServiceGateway implements ChatbotServiceOutputPort {

    @Override
    public void sendMessage(String message) {
        // Lógica para se comunicar com o Chatbot Service (HTTP, fila, etc.)
        // ...
        System.out.println("Enviando mensagem para o Chatbot Service: " + message);
    }
}