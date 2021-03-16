package zw.co.zss.interview.book;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import zw.co.zss.interview.book.dto.BookDTO;
import zw.co.zss.interview.common.ResponseTemplate;

@RestController
@RequestMapping("/book")
@CrossOrigin(origins = "*")
@Api(tags = "book")
@Validated
public class BookController {
    @Autowired
    private BookServiceImpl bookService;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Create new Book", response = ResponseTemplate.class)
    public ResponseTemplate<Book> createBook(@ApiParam("BookDTO") @RequestBody BookDTO bookDTO) {
        return bookService.createBook(bookDTO);
    }

    @PutMapping(path = "/{bookId}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Updates new Book", response = ResponseTemplate.class)
    public ResponseTemplate<Book> updateAccount(@ApiParam("UpdateAccountDTO") @RequestBody BookDTO bookDTO,@ApiParam("bookId") @PathVariable long bookId) {
        return bookService.createBook(bookDTO);
    }
}
