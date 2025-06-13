package org.pawlak.rentalApp.service;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pawlak.rentalApp.dao.BookDao;
import org.pawlak.rentalApp.dao.UserDao;
import org.pawlak.rentalApp.model.Book;
import org.pawlak.rentalApp.model.User;
import org.pawlak.rentalApp.model.enums.BookGenres;
import org.pawlak.rentalApp.model.enums.UserRole;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BookServiceTest {
    private BookDao bookDao;
    private BookService bookService;

    @BeforeEach
    public void setUp() {
        bookDao = mock(BookDao.class);
        bookService = new BookService(bookDao);
    }

    @Test
    void shouldReturnAllBooks() {
        List<Book> books = List.of(
                new Book(1, "Dune", "Frank Herbert", "Epic science fiction novel set on the desert planet Arrakis.", 1965, 412, BookGenres.SCIENCE_FICTION,  0, 0,true, 0),
                new Book(2, "The Hobbit", "J.R.R. Tolkien", "Fantasy adventure about Bilbo Baggins’ journey with dwarves.", 1937, 310, BookGenres.FANTASY,  0, 0,true, 0)
        );
        when(bookDao.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(books.get(0));
        assertThat(result.get(1)).isEqualTo(books.get(1));
        verify(bookDao).findAll();
    }

    @Test
    void shouldReturnBookById() {
        Book book = new Book(1, "The Hobbit", "J.R.R. Tolkien", "Fantasy adventure about Bilbo Baggins’ journey with dwarves.", 1937, 310, BookGenres.FANTASY,  0, 0,true, 0);
        when(bookDao.findById(1)).thenReturn(book);

        Optional<Book> result = bookService.getBookById(1);
        assertThat(result).contains(book);
    }

    @Test
    void shouldAddNewBook() {
        Book book = new Book(1, "The Hobbit", "J.R.R. Tolkien", "Fantasy adventure about Bilbo Baggins’ journey with dwarves.", 1937, 310, BookGenres.FANTASY,  0, 0,true, 0);
        bookService.addBook(book);
        verify(bookDao).insert(book);
    }

    @Test
    void shouldUpdateBook() {
        Book book = new Book(1, "Wiedźmin: ostatnie życzenie", "Andrzej Sapkowski", "Pierwszy tom opowiadań o Wiedźminie Geralcie", 1993, 330, BookGenres.FANTASY,  0, 0,true, 0);
        bookService.updateBook(book);
        verify(bookDao).update(book);
    }

    @Test
    void shouldDeleteBook() {
        int bookId = 42;
        bookService.deleteBook(bookId);
        verify(bookDao).delete(bookId);
    }

    @Test
    void shouldReturnAvailableBooks() {
        List<Book> availableBooks = List.of(
                new Book(1, "The Hobbit", "J.R.R. Tolkien", "Fantasy adventure about Bilbo Baggins’ journey with dwarves.", 1937, 310, BookGenres.FANTASY,  0, 0,true, 0),
                new Book(1, "Wiedźmin: ostatnie życzenie", "Andrzej Sapkowski", "Pierwszy tom opowiadań o Wiedźminie Geralcie", 1993, 330, BookGenres.FANTASY,  0, 0,true, 0)
        );
        when(bookDao.getAvailableBooks()).thenReturn(availableBooks);
        List<Book> result = bookService.getAvailableBooks();
        assertThat(result).isEqualTo(availableBooks);
        verify(bookDao).getAvailableBooks();
    }

    @Test
    void settersShouldUpdateValues() {
        Book book = new Book(1, "Old Title", "Old Author", "Old Desc", 1999, 100, BookGenres.DRAMA,  0, 0,true, 0);

        book.setTitle("New Title");
        book.setAuthor("New Author");
        book.setDescription("New Description");
        book.setReleaseYear(2025);
        book.setPageCount(500);
        book.setGenre(BookGenres.SCIENCE_FICTION);
        book.setAvailable(false);

        assertThat(book.getTitle()).isEqualTo("New Title");
        assertThat(book.getAuthor()).isEqualTo("New Author");
        assertThat(book.getDescription()).isEqualTo("New Description");
        assertThat(book.getReleaseYear()).isEqualTo(2025);
        assertThat(book.getPageCount()).isEqualTo(500);
        assertThat(book.getGenre()).isEqualTo(BookGenres.SCIENCE_FICTION);
        assertThat(book.isAvailable()).isFalse();
    }

    @Test
    public void shouldReturnZeroWhenNoRatings() {
        Book book = new Book(1, "Tytuł", "Autor", "Opis", 2020, 300, null, 0, 0, true, 0);
        assertEquals(0.0, book.getRating(), 0.001);
    }

    @Test
    public void shouldReturnCorrectAverageRating() {
        Book book = new Book(1, "Tytuł", "Autor", "Opis", 2020, 300, null, 5, 30, true, 0);
        assertEquals(6.0, book.getRating(), 0.001);
    }

    @Test
    void testSetTimesRented() {
        Book book = new Book(1, "Tytuł", "Autor", "Opis", 2020, 300, null, 0, 0, true, 0);
        book.setTimesRented(5);

        assertEquals(5, book.getTimesRented());
    }
}
