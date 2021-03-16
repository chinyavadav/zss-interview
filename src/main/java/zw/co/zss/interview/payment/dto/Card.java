package zw.co.zss.interview.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Card {
    private String id;
    private String expiry;
}
