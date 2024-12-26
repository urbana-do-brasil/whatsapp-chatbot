package br.com.urbana.apigateway.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
public class SignatureVerifier {

    @Value("${whatsapp.app_secret}")
    private String appSecret;

    public boolean verifySignature(ServerHttpRequest request, Map<String, Object> payload) {
        String signatureHeader = request.getHeaders().getFirst("X-Hub-Signature-25519");

        if (!StringUtils.hasText(signatureHeader) || !signatureHeader.startsWith("sha256=")) {
            return false; // Cabeçalho de assinatura ausente ou inválido
        }

        String receivedSignature = signatureHeader.substring("sha256=".length());

        // Calcular o HMAC-SHA256 do payload com o App Secret
        String calculatedSignature = calculateHMACSHA256(payload, appSecret);

        // Comparar as assinaturas (em Base64)
        return receivedSignature.equals(calculatedSignature);
    }

    private String calculateHMACSHA256(Map<String, Object> payload, String appSecret) {
        try {
            // Converter o payload para String (JSON)
            ObjectMapper objectMapper = new ObjectMapper();
            String payloadString = objectMapper.writeValueAsString(payload);

            // Converter o App Secret para byte array
            byte[] secretBytes = Base64.getDecoder().decode(appSecret);

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretBytes, "HmacSHA256");
            mac.init(secretKeySpec);

            byte[] hmacBytes = mac.doFinal(payloadString.getBytes());

            // Retorna a assinatura em Base64
            return Base64.getEncoder().encodeToString(hmacBytes);

        } catch (NoSuchAlgorithmException | InvalidKeyException | JsonProcessingException e) {
            log.error("Erro ao calcular HMAC-SHA256: {}", e.getMessage());
            return null;
        }
    }
}
