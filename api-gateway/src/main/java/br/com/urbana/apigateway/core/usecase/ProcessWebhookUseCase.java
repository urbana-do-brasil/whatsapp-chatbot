package br.com.urbana.apigateway.core.usecase;

import br.com.urbana.apigateway.core.inbound.WebhookInputPort;
import br.com.urbana.apigateway.core.outbound.ChatbotServiceOutputPort;
import br.com.urbana.apigateway.core.service.SignatureVerifier;
import br.com.urbana.apigateway.model.WhatsAppMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class ProcessWebhookUseCase implements WebhookInputPort {

    private final ChatbotServiceOutputPort chatbotService;
    private final SignatureVerifier signatureVerifier;

    @Override
    public Mono<Void> handleWebhook(ServerHttpRequest request, Map<String, Object> payload) {
        log.info("Mensagem recebida via Webhook: {}", payload);

        return Mono.fromSupplier(() -> {
                if (!signatureVerifier.verifySignature(request, payload)) { // Usa o SignatureVerifier
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Assinatura inválida");
                }
                return payload; // Retorna o payload se a assinatura for válida
            })
            .flatMap(validPayload -> Mono.fromRunnable(() -> processMessage(validPayload))); // Processa a mensagem reativamente
    }

    private void processMessage(Map<String, Object> payload) {
        WhatsAppMessage message = extractMessageFromPayload(payload);

        if (message != null && message.getType().equals("text")) {
            chatbotService.sendMessage(message);
        }
    }

    private WhatsAppMessage extractMessageFromPayload(Map<String, Object> payload) {
        try {
            // 1. Navega até a lista de mensagens
            List<Map<String, Object>> entries = (List<Map<String, Object>>) payload.get("entry");
            if (entries == null || entries.isEmpty()) {
                return null; // Ou lançar uma exceção, se apropriado
            }

            List<Map<String, Object>> changes = (List<Map<String, Object>>) entries.get(0).get("changes");
            if (changes == null || changes.isEmpty()) {
                return null;
            }

            Map<String, Object> value = (Map<String, Object>) changes.get(0).get("value");
            if (value == null ) {
                return null;
            }

            List<Map<String, Object>> messages = (List<Map<String, Object>>) value.get("messages");
            if (messages == null || messages.isEmpty()) {
                return null;
            }

            // 2. Extrai os dados da mensagem
            Map<String, Object> messageData = messages.get(0);

            // 3. Cria o objeto WhatsAppMessage
            WhatsAppMessage message = new WhatsAppMessage();
            message.setId((String) messageData.get("id"));
            message.setType((String) messageData.get("type"));
            message.setFrom((String) messageData.get("from"));
            message.setTimestamp((String) messageData.get("timestamp"));

            // 4. Extrai o conteúdo da mensagem de texto (se aplicável)
            if (message.getType().equals("text")) {
                Map<String, Object> textData = (Map<String, Object>) messageData.get("text");
                WhatsAppMessage.TextMessageContent textContent = new WhatsAppMessage.TextMessageContent();
                textContent.setBody((String) textData.get("body"));
                message.setText(textContent);

            }
            // 5. Retorna o objeto WhatsAppMessage
            return message;

        } catch (ClassCastException | NullPointerException e) {
            // Trate as exceções adequadamente (log, relançar uma exceção mais específica, etc.)
            log.error("Erro ao extrair mensagem do payload: {} Payload: {}", e.getMessage(), payload);
            return null; // Ou lançar uma exceção
        }
    }
}