package br.com.urbana.apigateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppMessage {

    private String id;
    private String type;
    private String from;
    private String timestamp;
    private TextMessageContent text;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TextMessageContent {

        private String body;
    }
}
