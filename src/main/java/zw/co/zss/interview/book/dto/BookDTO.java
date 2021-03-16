package zw.co.zss.interview.book.dto;

import lombok.Data;

@Data
public class BookDTO {
    private long isbn;
    private String title;
    private String description;
    private double price;
    private long categoryId;
    private long qtyInStock;
}
