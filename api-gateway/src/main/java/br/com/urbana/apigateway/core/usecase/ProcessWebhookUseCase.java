package br.com.urbana.apigateway.core.usecase;

import br.com.urbana.apigateway.core.inbound.WebhookInputPort;
import br.com.urbana.apigateway.core.outbound.ChatbotServiceOutputPort;
import br.com.urbana.apigateway.core.service.SignatureVerifier;
import br.com.urbana.apigateway.model.WhatsAppMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@AllArgsConstructor
public class ProcessWebhookUseCase implements WebhookInputPort {

    private final ChatbotServiceOutputPort chatbotService;
    private final SignatureVerifier signatureVerifier;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handleWebhook(String signature, String payload) {
        log.info("Mensagem recebida via Webhook: {}", payload);

        if (!signatureVerifier.verifySignature(signature, payload)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Assinatura inválida");
        }

        processMessage(payload);

        return Mono.empty();
    }

    private void processMessage(String payload) {
        WhatsAppMessage message = extractMessageFromPayload(payload);
        log.info("Mensagem extraída - {}", message);

        if (message != null && message.getType().equals("text")) {
            chatbotService.sendMessage(message);
        }
    }

    private WhatsAppMessage extractMessageFromPayload(String payload) {
        try {
            JsonNode payloadJsonNode = objectMapper.readTree(payload);
            JsonNode entry = payloadJsonNode.path("entry").get(0);

            if (entry == null || entry.isEmpty()) {
                log.info("Campo 'entry' não encontrado");
                return null;
            }

            JsonNode changes = entry.path("changes").get(0);
            if (changes == null || changes.isEmpty()) {
                log.info("Campo 'changes' não encontrado");
                return null;
            }

            JsonNode value = changes.path("value");
            if (value == null ) {
                log.info("Campo 'changes' não encontrado");
                return null;
            }

            JsonNode messages = value.path("messages");
            if (messages == null || messages.isEmpty()) {
                log.info("Campo 'changes' não encontrado");
                return null;
            }

            return getWhatsAppMessage(messages);

        } catch (JsonProcessingException e) {
            log.error("Erro ao extrair mensagem do payload: {} Payload: {}", e.getMessage(), payload);
            return null;
        }
    }

    private WhatsAppMessage getWhatsAppMessage(JsonNode messages) throws JsonProcessingException {
        JsonNode messageNode = messages.get(0);
        return objectMapper.treeToValue(messageNode, WhatsAppMessage.class);
    }
}