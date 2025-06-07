package org.pawlak.rentalApp.service;

import org.pawlak.rentalApp.dao.BookDao;
import org.pawlak.rentalApp.model.Book;

import java.util.List;
import java.util.Optional;

public class BookService {
    private final BookDao bookDao;

    public BookService(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    public List<Book> getAllBooks() {
        return bookDao.findAll();
    }

    public Optional<Book> getBookById(int id) {
        Book book = bookDao.findById(id);
        return Optional.ofNullable(book);
    }

    public void addBook(Book book) {
        bookDao.insert(book);
    }

    public void updateBook(Book book) {
        bookDao.update(book);
    }

    public void deleteBook(int id) {
        bookDao.delete(id);
    }

    public List<Book> getAvailableBooks() {
        return bookDao.getAvailableBooks();
    }

    public boolean rentBook(int bookId) {
        Book book = bookDao.findById(bookId);
        if (book != null && book.isAvailable()) {
            book.setAvailable(false);
            bookDao.update(book);
            return true;
        }
        return false;
    }

    public boolean returnBook(int bookId) {
        Book book = bookDao.findById(bookId);
        if (book != null && !book.isAvailable()) {
            book.setAvailable(true);
            bookDao.update(book);
            return true;
        }
        return false;
    }
}
