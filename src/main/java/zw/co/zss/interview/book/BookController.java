package zw.co.zss.interview.book;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import zw.co.zss.interview.book.dto.BookDTO;
import zw.co.zss.interview.book.dto.PurchaseDTO;
import zw.co.zss.interview.category.Category;
import zw.co.zss.interview.category.CategoryServiceImpl;
import zw.co.zss.interview.common.ResponseTemplate;
import zw.co.zss.interview.payment.Payment;

import java.util.List;

@RestController
@RequestMapping("/book")
@CrossOrigin(origins = "*")
@Api(tags = "book")
@Validated
public class BookController {

    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private CategoryServiceImpl categoryService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Fetches all Books", response = ResponseTemplate.class)
    public ResponseTemplate<List<Book>> getAllBooks() {
        return bookService.getBooks();
    }

    @GetMapping(path = "/category", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Fetches all Book Categories", response = ResponseTemplate.class)
    public ResponseTemplate<List<Category>> getAllCategories() {
        return categoryService.getCategories();
    }


    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Create new Book", response = ResponseTemplate.class)
    public ResponseTemplate<Book> createBook(@ApiParam("BookDTO") @RequestBody BookDTO bookDTO) {
        return bookService.createBook(bookDTO);
    }

    @PutMapping(path = "/{bookId}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Updates new Book", response = ResponseTemplate.class)
    public ResponseTemplate<Book> updateBook(@ApiParam("UpdateAccountDTO") @RequestBody BookDTO bookDTO, @ApiParam("bookId") @PathVariable long bookId) {
        return bookService.updateBook(bookId, bookDTO);
    }

    @GetMapping(path = "/category/{categoryId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Books by Category", response = ResponseTemplate.class)
    public ResponseTemplate<List<Book>> booksByCategory(@ApiParam("categoryId") @PathVariable long categoryId) {
        return bookService.getBooks(categoryId);
    }

    @PostMapping(path = "/purchase", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Purchase Book", response = ResponseTemplate.class)
    public ResponseTemplate<Payment> purchaseBook(@ApiParam("PurchaseDTO") @RequestBody PurchaseDTO purchaseDTO) {
        return bookService.purchaseBook(purchaseDTO);
    }
}
