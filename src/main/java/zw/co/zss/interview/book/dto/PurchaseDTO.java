package zw.co.zss.interview.book.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class PurchaseDTO {
    private String email;
    private long bookId;
    private String pan;
    private String expiry;
    private String addressLine1;
    private String city;
}
