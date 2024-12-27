package br.com.urbana.apigateway.core.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Security;

@Slf4j
@Component
public class SignatureVerifier {

    private static final String PREFIX_SHA256 = "sha256=";

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
        Security.addProvider(new BouncyCastleProvider());

        byte[] keyBytes = appSecret.getBytes(StandardCharsets.UTF_8);
        byte[] payloadBytes = payloadString.getBytes(StandardCharsets.UTF_8);

        HMac hmac = new HMac(new SHA256Digest());
        hmac.init(new KeyParameter(keyBytes));
        hmac.update(payloadBytes, 0, payloadBytes.length);

        byte[] hmacBytes = new byte[hmac.getMacSize()];
        hmac.doFinal(hmacBytes, 0);

        return Hex.encodeHexString(hmacBytes);
    }
}
