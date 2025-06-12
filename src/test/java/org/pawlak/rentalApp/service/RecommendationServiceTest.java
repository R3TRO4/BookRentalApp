package org.pawlak.rentalApp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pawlak.rentalApp.dao.BookDao;
import org.pawlak.rentalApp.model.Book;
import org.pawlak.rentalApp.model.User;
import org.pawlak.rentalApp.model.enums.BookGenres;
import org.pawlak.rentalApp.model.enums.UserRole;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class RecommendationServiceTest {

    private BookDao bookDao;
    private RecommendationService recommendationService;

    @BeforeEach
    public void setUp() {
        bookDao = mock(BookDao.class);
        recommendationService = new RecommendationService(bookDao);
    }

    @Test
    void shouldReturnAvailableBooksByUserFavoriteGenre() {
        User user = new User(1, "Alice", "alice@test.com", "pass", BookGenres.FANTASY, UserRole.USER);
        List<Book> fantasyBooks = List.of(
                new Book(1, "Wiedźmin: ostatnie życzenie", "Andrzej Sapkowski", "Pierwszy tom opowiadań o Wiedźminie Geralcie", 1993, 330, BookGenres.FANTASY,  0, 0,true)
        );

        when(bookDao.findAvailableBooksByGenre("FANTASY")).thenReturn(fantasyBooks);
        List<Book> result = recommendationService.getAvailableBooksByUserFavoriteGenre(user);
        assertThat(result).isEqualTo(fantasyBooks);
        verify(bookDao).findAvailableBooksByGenre("FANTASY");
    }

    @Test
    public void shouldReturnTop10AvailableBooksSortedByRating() {
        // given
        List<Book> books = IntStream.range(0, 15)
                .mapToObj(i -> {
                    Book b = new Book(i, "Tytuł " + i, "Autor", "Opis", 2020, 300, null, i * 10, i, true);
                    return b;
                })
                .collect(Collectors.toList());

        when(bookDao.findAll()).thenReturn(books);

        // when
        List<Book> topBooks = recommendationService.getAvailableBooksByRating();

        // then
        assertEquals(10, topBooks.size());
        for (int i = 0; i < topBooks.size() - 1; i++) {
            assertTrue(topBooks.get(i).getRating() >= topBooks.get(i + 1).getRating());
        }
    }
}
