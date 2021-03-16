package zw.co.zss.interview.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zw.co.zss.interview.book.Book;

@Service
public class BookServiceImpl {
    @Autowired
    private BookRepository bookRepository;

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
}
