package zw.co.zss.interview.book;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import zw.co.zss.interview.book.dto.BookDTO;
import zw.co.zss.interview.common.ResponseTemplate;
import zw.co.zss.interview.exception.CustomException;

@Service
public class BookServiceImpl {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ModelMapper modelMapper;

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


    public ResponseTemplate<Book> createBook(BookDTO bookDTO) {
        Book existingBook = findBookByISBN(bookDTO.getIsbn());
        if (existingBook == null) {
            Book book = modelMapper.map(bookDTO, Book.class);
            Book savedBook = saveBook(book);
            return new ResponseTemplate<>("success", "Book successfully added!", savedBook);
        }
        throw new CustomException("Book has Duplicate ISBN!", HttpStatus.CONFLICT);
    }

    public ResponseTemplate<Book> updateBook(long bookId, BookDTO bookDTO) {
        Book book = findBookById(bookId);
        if (book != null) {
            book = modelMapper.map(bookDTO, Book.class);
            Book savedBook = saveBook(book);
            return new ResponseTemplate<>("success", "Book successfully updated!", savedBook);
        }
        throw new CustomException("Book does not exist!", HttpStatus.NOT_FOUND);
    }
}
