package br.com.urbana.apigateway.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
@Component
public class SignatureVerifier {

    private static final String PREFIX_SHA256 = "sha256=";
    private static final String ENCODING_ALGORITHM = "HmacSHA256";

    @Value("${whatsapp.app_secret}")
    private String appSecret;

    public boolean verifySignature(String signature, String payloadString) {
        log.info("Assinatura recebida pelo WhatsApp - {}", signature);
        if (!StringUtils.hasText(signature) || !signature.startsWith(PREFIX_SHA256)) {
            log.error("Assinatura inválida - {}", signature);
            return false;
        }

        String receivedSignature = signature.substring(PREFIX_SHA256.length());

        String calculatedSignature = calculateHMACSHA256(payloadString, appSecret);
        log.info("Assinatura HMAC-SHA256 - {}", calculatedSignature);

        return receivedSignature.equals(calculatedSignature);
    }

    private String calculateHMACSHA256(String payloadString, String appSecret) {
        try {
            Mac mac = Mac.getInstance(ENCODING_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(appSecret.getBytes(), ENCODING_ALGORITHM);
            mac.init(secretKeySpec);

            byte[] hmacBytes = mac.doFinal(payloadString.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hmacBytes);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Erro ao calcular HMAC-SHA256: {}", e.getMessage());
            return null;
        }
    }
}
