package zw.co.zss.interview.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import zw.co.zss.interview.payment.dto.TransactionRequest;

@Service
public class PaymentServiceImpl {
    Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Value("${payment-gateway.endpoint}")
    private String endpoint;

    @Value("${payment-gateway.api-key}")
    private String apiKey;

    private final WebClient webClient;

    public PaymentServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(endpoint)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("Content-Type", "application/json");
                    httpHeaders.set("Authorization", "Bearer " + apiKey);
                })
                .build();
    }

    public String executeTransaction(TransactionRequest transactionRequest) {
        return this.webClient.post()
                .uri("/api/transaction")
                .body(Mono.just(transactionRequest), TransactionRequest.class)
                .exchange()
                .flatMap(clientResponse -> {
                    if (clientResponse.statusCode().is5xxServerError()) {
                        clientResponse.body((clientHttpResponse, context) -> {
                            return clientHttpResponse.getBody();
                        });
                        return clientResponse.bodyToMono(String.class);
                    } else {
                        return clientResponse.bodyToMono(String.class);
                    }
                })
                .block();
    }
}
