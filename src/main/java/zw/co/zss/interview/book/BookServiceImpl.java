package zw.co.zss.interview.book;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import zw.co.zss.interview.book.dto.BookDTO;
import zw.co.zss.interview.book.dto.PurchaseDTO;
import zw.co.zss.interview.common.ResponseTemplate;
import zw.co.zss.interview.exception.CustomException;
import zw.co.zss.interview.payment.Payment;
import zw.co.zss.interview.payment.PaymentServiceImpl;
import zw.co.zss.interview.payment.PaymentStatus;
import zw.co.zss.interview.payment.dto.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service
public class BookServiceImpl {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PaymentServiceImpl paymentService;

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
            Book book = modelMapper.map(bookDTO, Book.class);
            Book savedBook = saveBook(book);
            return new ResponseTemplate<>("success", "Book successfully added!", savedBook);
        }
        throw new CustomException("Book has Duplicate ISBN!", HttpStatus.CONFLICT);
    }

    // Updates Existing Book
    public ResponseTemplate<Book> updateBook(long bookId, BookDTO bookDTO) {
        Book book = findBookById(bookId);
        if (book != null) {
            book = modelMapper.map(bookDTO, Book.class);
            Book savedBook = saveBook(book);
            return new ResponseTemplate<>("success", "Book successfully updated!", savedBook);
        }
        throw new CustomException("Book does not exist!", HttpStatus.NOT_FOUND);
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
                return pan.substring(0, 5) + "xxxxxx" + pan.substring(12, 15);
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
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    dateFormat.parse(purchaseDTO.getExpiry());
                } catch (ParseException e) {
                    throw new CustomException("Card Expiry Date is in wrong format!", HttpStatus.BAD_REQUEST);
                }

                TransactionRequest transactionRequest = new TransactionRequest(TransactionType.PURCHASE, ExtendedType.NONE, book.getPrice(), new Card(purchaseDTO.getPan(), purchaseDTO.getExpiry()), savedPayment.getPaymentId().toString(), narration, additionalData);
                TransactionResponse transactionResponse = paymentService.executeTransaction(transactionRequest);
                if (transactionResponse.getResponseCode().equals("000")) {
                    // Deduct from Stock
                    // TODO research concurrency effects on persistence
                    book.setQtyInStock(book.getQtyInStock() - 1);
                    saveBook(book);
                    savedPayment.setStatus(PaymentStatus.SUCCESS);
                    savedPayment.setResponseCode(transactionResponse.getResponseCode());
                    Payment updatedPayment = paymentService.savePayment(savedPayment);
                    // TODO send email notification
                    return new ResponseTemplate("success", "Purchase was successful", updatedPayment);
                }

                savedPayment.setStatus(PaymentStatus.FAILED);
                savedPayment.setResponseCode(transactionResponse.getResponseCode());
                paymentService.savePayment(savedPayment);
                throw new CustomException(String.format("Payment was not successful! Use reference: %s for any queries", savedPayment.getPaymentId()), HttpStatus.EXPECTATION_FAILED);
            }
            throw new CustomException("Book is out of stock!", HttpStatus.EXPECTATION_FAILED);
        }
        throw new CustomException("Book does not exist!", HttpStatus.NOT_FOUND);
    }
}
