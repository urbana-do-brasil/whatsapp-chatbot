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
                if (!signatureVerifier.verifySignature(request, payload)) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Assinatura inválida");
                }
                
                log.info("A mensagem recebida tem assinatura válida");
                return payload;
            })
            .flatMap(validPayload -> Mono.fromRunnable(() -> processMessage(validPayload)));
    }

    private void processMessage(Map<String, Object> payload) {
        WhatsAppMessage message = extractMessageFromPayload(payload);
        log.info("Mensagem extraída - {}", message);

        if (message != null && message.getType().equals("text")) {
            chatbotService.sendMessage(message);
        }
    }

    private WhatsAppMessage extractMessageFromPayload(Map<String, Object> payload) {
        try {
            List<Map<String, Object>> entries = (List<Map<String, Object>>) payload.get("entry");
            if (entries == null || entries.isEmpty()) {
                log.info("Campo 'entry' não encontrado");
                return null;
            }

            List<Map<String, Object>> changes = (List<Map<String, Object>>) entries.get(0).get("changes");
            if (changes == null || changes.isEmpty()) {
                log.info("Campo 'changes' não encontrado");
                return null;
            }

            Map<String, Object> value = (Map<String, Object>) changes.get(0).get("value");
            if (value == null ) {
                log.info("Campo 'changes' não encontrado");
                return null;
            }

            List<Map<String, Object>> messages = (List<Map<String, Object>>) value.get("messages");
            if (messages == null || messages.isEmpty()) {
                log.info("Campo 'changes' não encontrado");
                return null;
            }

            return getWhatsAppMessage(messages);

        } catch (ClassCastException | NullPointerException e) {
            log.error("Erro ao extrair mensagem do payload: {} Payload: {}", e.getMessage(), payload);
            return null;
        }
    }

    private static WhatsAppMessage getWhatsAppMessage(List<Map<String, Object>> messages) {
        Map<String, Object> messageData = messages.get(0);

        WhatsAppMessage message = new WhatsAppMessage();
        message.setId((String) messageData.get("id"));
        message.setType((String) messageData.get("type"));
        message.setFrom((String) messageData.get("from"));
        message.setTimestamp((String) messageData.get("timestamp"));

        if (message.getType().equals("text")) {
            Map<String, Object> textData = (Map<String, Object>) messageData.get("text");
            WhatsAppMessage.TextMessageContent textContent = new WhatsAppMessage.TextMessageContent();
            textContent.setBody((String) textData.get("body"));
            message.setText(textContent);

        }

        return message;
    }
}