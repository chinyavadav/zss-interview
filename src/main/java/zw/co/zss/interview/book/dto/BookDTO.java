package zw.co.zss.interview.book.dto;

import lombok.Data;

@Data
public class BookDTO {
    private String isbn;
    private String title;
    private String description;
    private double price;
}
