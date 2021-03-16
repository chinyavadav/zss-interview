package zw.co.zss.interview.common;

import lombok.*;
import org.springframework.web.bind.annotation.ResponseBody;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ResponseBody
public class ResponseTemplate<T> {
    private String status;
    private String message;
    private T data;
}
