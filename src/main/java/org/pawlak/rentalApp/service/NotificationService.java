package org.pawlak.rentalApp.service;

import org.pawlak.rentalApp.model.Rental;
import org.pawlak.rentalApp.model.User;
import org.pawlak.rentalApp.service.notifier.Notifier;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class NotificationService {
    private final RentalService rentalService;
    private final Notifier notifier;


    public NotificationService(RentalService rentalService, Notifier notifier) {
        this.rentalService = rentalService;
        this.notifier = notifier;
    }

    public void checkAndNotify(User user) {
        List<Rental> rentals = rentalService.getActiveRentalsForUser(user);
        Map<Long, String> messages = Map.of(
                3L, "Zostały 3 dni do zwrotu książki: ",
                2L, "Zostały 2 dni do zwrotu książki: ",
                1L, "Został 1 dzień do zwrotu książki: "
        );

        for (Rental r : rentals) {
            long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), r.getDueDate());
            if (messages.containsKey(daysLeft)) {
                notifier.notify(user, messages.get(daysLeft) + r.getBook().getTitle());
            }
        }
    }
}
