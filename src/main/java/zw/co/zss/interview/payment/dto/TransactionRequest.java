package zw.co.zss.interview.payment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;
import java.util.HashMap;

@Data
@RequiredArgsConstructor
public class TransactionRequest {
    @NonNull
    private TransactionType type;

    @NonNull
    private ExtendedType extendedType;

    @NonNull
    private double amount;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd’T’HH:mm:ss.SSSZ")
    private Date created;

    @NonNull
    private Card card;

    @NonNull
    private String reference;

    @NonNull
    private String narration;

    @NonNull
    private HashMap<String, Object> additionalData;
}
