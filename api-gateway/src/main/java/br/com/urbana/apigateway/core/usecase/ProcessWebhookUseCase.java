package br.com.urbana.apigateway.core.usecase;

import br.com.urbana.apigateway.core.inbound.WebhookInputPort;
import br.com.urbana.apigateway.core.outbound.ChatbotServiceOutputPort;
import br.com.urbana.apigateway.model.WhatsAppMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class ProcessWebhookUseCase implements WebhookInputPort {

    private final ChatbotServiceOutputPort chatbotService;

    public ProcessWebhookUseCase(ChatbotServiceOutputPort chatbotService) {
        this.chatbotService = chatbotService;
    }

    @Override
    public void handleWebhook(Map<String, Object> payload) {
        try {
            log.info("Mensagem recebida via Webhook: {}", payload);

            // Extrai a mensagem do payload (adaptado para estrutura do WhatsApp)
            WhatsAppMessage message = extractMessageFromPayload(payload);

            if (message != null && message.getType().equals("text")) {
                // ... (lógica para processar a mensagem)
                chatbotService.sendMessage(message); // Envia a mensagem para o Chatbot Service
            }

        } catch (Exception e) {
            log.error("Erro ao processar webhook:", e);
            // Tratar a exceção adequadamente (lançar uma exceção de negócio ou retornar um erro)
        }
    }

    private WhatsAppMessage extractMessageFromPayload(Map<String, Object> payload) {
        // Lógica para extrair a mensagem do payload complexo do WhatsApp.  Use um modelo `WhatsAppMessage`.
        // ... (implementação) ...
        return null; // Substitua pelo objeto WhatsAppMessage
    }
}