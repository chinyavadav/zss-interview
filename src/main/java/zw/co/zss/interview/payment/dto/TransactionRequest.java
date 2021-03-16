package zw.co.zss.interview.payment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;

@Data
@AllArgsConstructor
public class TransactionRequest {
    private TransactionType type;
    private ExtendedType extendedType;
    private double amount;
    private String created;
    private Card card;
    private String reference;
    private String narration;
    private HashMap<String, Object> additionalData;
}
