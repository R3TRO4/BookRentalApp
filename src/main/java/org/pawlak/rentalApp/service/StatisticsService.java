package org.pawlak.rentalApp.service;

import org.pawlak.rentalApp.dao.BookDao;
import org.pawlak.rentalApp.model.Book;
import org.pawlak.rentalApp.model.Rental;
import org.pawlak.rentalApp.model.User;

import java.util.*;
import java.util.stream.Collectors;

public class StatisticsService {

    private final RentalService rentalService;

    public StatisticsService(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    public long getTotalRentals() {
        return rentalService.getAllRentals().size();
    }

    public Book getMostPopularBook() {
        return rentalService.getAllRentals().stream()
                .collect(Collectors.groupingBy(Rental::getBook, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public List<Book> getTop10MostPopularBooks() {
        return rentalService.getAllRentals().stream()
                .collect(Collectors.groupingBy(Rental::getBook, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Book, Long>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<User> getTop5MostActiveUsers() {
        return rentalService.getAllRentals().stream()
                .collect(Collectors.groupingBy(Rental::getUser, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<User, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
