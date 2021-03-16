package zw.co.zss.interview.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import zw.co.zss.interview.book.Book;
import zw.co.zss.interview.payment.dto.TransactionRequest;
import zw.co.zss.interview.payment.dto.TransactionResponse;

import java.util.*;

@Service
public class PaymentServiceImpl {
    Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Value("${payment-gateway.endpoint}")
    private String endpoint;

    @Value("${payment-gateway.api-key}")
    private String apiKey;

    private List<String> responseCodes;

    private final WebClient webClient;

    @Autowired
    private PaymentRepository paymentRepository;

    public PaymentServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(endpoint)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("Content-Type", "application/json");
                    httpHeaders.set("Authorization", "Bearer " + apiKey);
                })
                .build();
        setResponseCodes();
    }

    // Create & Update
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    // Read
    public Payment findPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId).orElse(null);
    }

    private void setResponseCodes() {
        responseCodes = new ArrayList<>();
        responseCodes.add("000");
        responseCodes.add("001");
        responseCodes.add("005");
        responseCodes.add("012");
        responseCodes.add("096");
    }

    public TransactionResponse executeTransaction(TransactionRequest transactionRequest) {
        return this.webClient.post()
                .uri("/api/transaction")
                .body(Mono.just(transactionRequest), TransactionRequest.class)
                .retrieve()
                .bodyToMono(TransactionResponse.class)
                .block();
    }
}
