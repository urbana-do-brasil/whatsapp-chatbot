package br.com.urbana.apigateway.core.usecase;

import br.com.urbana.apigateway.core.inbound.WebhookInputPort;
import br.com.urbana.apigateway.core.outbound.ChatbotServiceOutputPort;
import br.com.urbana.apigateway.core.service.SignatureVerifier;
import br.com.urbana.apigateway.model.WhatsAppMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

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
        // Lógica para extrair a mensagem do payload complexo do WhatsApp.  Use um modelo `WhatsAppMessage`.
        // ... (implementação) ...
        return null; // Substitua pelo objeto WhatsAppMessage
    }
}