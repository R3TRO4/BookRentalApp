package org.pawlak.rentalApp.service;

import org.pawlak.rentalApp.model.Rental;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PenaltyService {

    private final RentalService rentalService;
    private final double basePenalty = 10.0;
    private final double dailyPenaltyRate = 4.0;

    public PenaltyService(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    public void applyPenalty(Rental rental) {
        LocalDate dueDate = rental.getDueDate();
        LocalDate returnDate = rental.getReturnDate();
        LocalDate effectiveDate = (returnDate != null) ? returnDate : LocalDate.now();

        long daysLate = ChronoUnit.DAYS.between(dueDate, effectiveDate);

        if (daysLate > 0) {
            double penalty = basePenalty + (daysLate - 1) * dailyPenaltyRate;
            System.out.println(rental.getId());
            System.out.println(penalty);
            rental.setPenaltyFee(penalty);
        } else {
            rental.setPenaltyFee(0.0);
        }

        rentalService.updateRental(rental);
    }
}
