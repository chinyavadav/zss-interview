package zw.co.zss.interview.book.dto;

import lombok.Data;

@Data
public class PurchaseDTO {
    private String email;
    private long bookId;
    private String pan;
    private String expiry;
    private String addressLine1;
    private String city;
}
