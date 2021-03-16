package zw.co.zss.interview.payment.dto;

import lombok.Data;

import java.util.Date;

@Data
public class TransactionResponse {
    private Date updated;
    private String responseCode;
    private String responseDescription;
    private String reference;
    private String debitReference;
}
