package br.com.urbana.apigateway.core.usecase;

import br.com.urbana.apigateway.core.inbound.WebhookInputPort;
import br.com.urbana.apigateway.core.outbound.ChatbotServiceOutputPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProcessWebhookUseCase implements WebhookInputPort {

    private final ChatbotServiceOutputPort chatbotService;

    public ProcessWebhookUseCase(ChatbotServiceOutputPort chatbotService) {
        this.chatbotService = chatbotService;
    }

    @Override
    public void handleWebhook(String payload) {
        try {
            log.info("Mensagem recebida via Webhook: {}", payload);

            // 1. Validar o payload (implementar a lógica de validação)
            // ...

            // 2. Transformar o payload, se necessário (implementar a lógica de transformação)
            String transformedPayload = transformPayload(payload); // Exemplo

            // 3. Chamar o Chatbot Service
            chatbotService.sendMessage(transformedPayload);

        } catch (Exception e) {
            log.error("Erro ao processar webhook:", e);
            // Tratar a exceção adequadamente (lançar uma exceção de negócio ou retornar um erro)
        }
    }

    private String transformPayload(String payload) {
        // Implementar a lógica de transformação do payload
        // ...
        return payload; // Exemplo (retorna o payload original sem transformação)
    }
}