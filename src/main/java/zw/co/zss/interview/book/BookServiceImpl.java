package zw.co.zss.interview.book;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zw.co.zss.interview.book.dto.BookDTO;
import zw.co.zss.interview.common.ResponseTemplate;

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

    // Delete
    public void deleteBook(Book book) {
        bookRepository.delete(book);
    }


    public ResponseTemplate<Book> createBook(BookDTO bookDTO) {
        Book book = modelMapper.map(bookDTO, Book.class);
        Book savedBook = saveBook(book);
        return new ResponseTemplate<>("success", "Book successfully added!", savedBook);
    }
}
