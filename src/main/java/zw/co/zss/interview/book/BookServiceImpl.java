package zw.co.zss.interview.book;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import zw.co.zss.interview.book.dto.BookDTO;
import zw.co.zss.interview.book.dto.PurchaseDTO;
import zw.co.zss.interview.category.Category;
import zw.co.zss.interview.category.CategoryServiceImpl;
import zw.co.zss.interview.common.ResponseTemplate;
import zw.co.zss.interview.exception.CustomException;
import zw.co.zss.interview.payment.Payment;
import zw.co.zss.interview.payment.PaymentServiceImpl;
import zw.co.zss.interview.payment.PaymentStatus;
import zw.co.zss.interview.payment.dto.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class BookServiceImpl {
    Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    @Autowired
    BookRepository bookRepository;

    @Autowired
    CategoryServiceImpl categoryService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PaymentServiceImpl paymentService;

    // Create & Update
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    // Read
    public Book findBookById(long bookId) {
        return bookRepository.findById(bookId).orElse(null);
    }

    public Book findBookByISBN(long isbn) {
        return bookRepository.findByIsbn(isbn).orElse(null);
    }

    // Delete
    public void deleteBook(Book book) {
        bookRepository.delete(book);
    }


    // Create new Book
    public ResponseTemplate<Book> createBook(BookDTO bookDTO) {
        Book existingBook = findBookByISBN(bookDTO.getIsbn());
        if (existingBook == null) {
            Category category = categoryService.findCategoryById(bookDTO.getCategoryId());
            if (category != null) {
                Book book = new Book(bookDTO.getIsbn(), bookDTO.getTitle(), bookDTO.getDescription(), bookDTO.getPrice(), bookDTO.getQtyInStock(), category);
                Book savedBook = saveBook(book);
                return new ResponseTemplate<>("success", "Book successfully added!", savedBook);
            }
            throw new CustomException("Category does not exist!", HttpStatus.CONFLICT);
        }
        throw new CustomException("Book has Duplicate ISBN!", HttpStatus.CONFLICT);
    }

    // Updates Existing Book
    public ResponseTemplate<Book> updateBook(long bookId, BookDTO bookDTO) {
        Book book = findBookById(bookId);
        if (book != null) {
            Category category = categoryService.findCategoryById(bookDTO.getCategoryId());
            if (category != null) {
                modelMapper.map(bookDTO, book);
                book.setCategory(category);
                Book savedBook = saveBook(book);
                return new ResponseTemplate<>("success", "Book successfully updated!", savedBook);
            }
            throw new CustomException("Category does not exist!", HttpStatus.CONFLICT);
        }
        throw new CustomException("Book does not exist!", HttpStatus.NOT_FOUND);
    }

    // Fetches all Books
    public ResponseTemplate<List<Book>> getBooks() {
        List<Book> books = bookRepository.findAll();
        return new ResponseTemplate<>("success", "Books Found!", books);
    }

    // Fetches Books by Category
    public ResponseTemplate<List<Book>> getBooks(long categoryId) {
        List<Book> books = bookRepository.findAllByCategory_CategoryId(categoryId);
        if (books.size() > 0) {
            new ResponseTemplate<>("success", "Books Found!", books);
        }
        throw new CustomException("No books found in the category!", HttpStatus.NOT_FOUND);
    }

    // Validates PAN and Masks
    private String validateAndMaskPan(String pan) {
        try {
            Long.parseLong(pan);
            if (pan.length() == 16) {
                return pan.substring(0, 6) + "xxxxxx" + pan.substring(12, 16);
            }
        } catch (Exception ignore) {
        }
        throw new CustomException("PAN must be 16 digit!", HttpStatus.NOT_FOUND);
    }


    public ResponseTemplate purchaseBook(PurchaseDTO purchaseDTO) {
        Book book = findBookById(purchaseDTO.getBookId());
        if (book != null) {
            if (book.getQtyInStock() > 0) {
                String maskedPan = validateAndMaskPan(purchaseDTO.getPan());
                Payment payment = new Payment(book, book.getPrice(), maskedPan, purchaseDTO.getEmail(), purchaseDTO.getAddressLine1(), purchaseDTO.getCity());
                Payment savedPayment = paymentService.savePayment(payment);

                // Prepare Transaction
                String narration = String.format("Purchase of book: %s", book.getTitle());
                HashMap<String, Object> additionalData = new HashMap<>();
                additionalData.put("bookId", book.getBookId());
                additionalData.put("email", purchaseDTO.getEmail());

                // Validate Expiry Date
                try {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    dateFormat.parse(purchaseDTO.getExpiry());
                } catch (ParseException e) {
                    throw new CustomException("Card Expiry Date is in wrong format!", HttpStatus.BAD_REQUEST);
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                String created = simpleDateFormat.format(new Date());
                TransactionRequest transactionRequest = new TransactionRequest(TransactionType.PURCHASE, ExtendedType.NONE, book.getPrice(), created, new Card(purchaseDTO.getPan(), purchaseDTO.getExpiry()), savedPayment.getPaymentId().toString(), narration, additionalData);
                try {
                    TransactionResponse transactionResponse = paymentService.executeTransaction(transactionRequest);
                    logger.info(new Gson().toJson(transactionResponse));
                    if (transactionResponse.getResponseCode().equals("000")) {
                        try {
                            // Deduct from Stock
                            // TODO research concurrency effects on persistence
                            book.setQtyInStock(book.getQtyInStock() - 1);
                            saveBook(book);
                            savedPayment.setStatus(PaymentStatus.SUCCESS);
                            savedPayment.setResponseCode(transactionResponse.getResponseCode());
                            Payment updatedPayment = paymentService.savePayment(savedPayment);
                            // TODO send email notification
                            return new ResponseTemplate("success", "Purchase was successful", updatedPayment);
                        } catch (Exception e) {
                            logger.error("critical error, manually resolve: " + payment.getPaymentId());
                            throw new CustomException("An issue occurred with your order!", HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    }
                    savedPayment.setStatus(PaymentStatus.FAILED);
                    savedPayment.setResponseCode(transactionResponse.getResponseCode());
                    paymentService.savePayment(savedPayment);
                    String message;
                    switch (transactionResponse.getResponseCode()) {
                        case "001":
                            message = "Error processing payment please contact your bank!";
                            break;
                        case "005":
                            message = "Failed to process your payment! Contact support if funds were deducted!";
                            break;
                        case "012":
                            message = "Failed to process transaction! Please make sure your card number and expiry date are correct!";
                            break;
                        case "096":
                            message = "Error occured while processing your transaction! Contact our support if funds were deducted from your account!";
                            break;
                        default:
                            message = "Payment was not successful!";
                            break;
                    }
                    throw new CustomException(message, HttpStatus.EXPECTATION_FAILED);
                } catch (Exception e) {
                    logger.error("critical error, followup: " + payment.getPaymentId());
                    savedPayment.setStatus(PaymentStatus.FAILED);
                    paymentService.savePayment(savedPayment);
                    throw new CustomException("Error occurred while processing your transaction! If funds were deducted contact our support team!", HttpStatus.EXPECTATION_FAILED);
                }

            }
            throw new CustomException("Book is out of stock!", HttpStatus.EXPECTATION_FAILED);
        }
        throw new CustomException("Book does not exist!", HttpStatus.NOT_FOUND);
    }
}
