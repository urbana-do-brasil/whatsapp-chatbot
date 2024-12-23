FROM openjdk:17-alpine

ARG whatsapp_verify_token
ARG whatsapp_graph_api_token

ENV whatsapp_verify_token=${whatsapp_verify_token}
ENV whatsapp_graph_api_token=${whatsapp_graph_api_token}

WORKDIR /app

COPY target/api-gateway.jar /app/api-gateway.jar

EXPOSE 8080

CMD ["java", "-jar", "api-gateway.jar"]