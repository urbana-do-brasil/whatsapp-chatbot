package br.com.urbana.apigateway.gateway;

import br.com.urbana.apigateway.core.outbound.ChatbotServiceOutputPort;
import br.com.urbana.apigateway.model.WhatsAppMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatbotServiceGateway implements ChatbotServiceOutputPort {

    @Override
    public void sendMessage(WhatsAppMessage message) {
        // Lógica para se comunicar com o Chatbot Service (HTTP, fila, etc.)
        // ...
        log.info("Enviando mensagem para o Chatbot Service: " + message);
    }
}