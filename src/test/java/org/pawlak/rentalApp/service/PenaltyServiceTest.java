package org.pawlak.rentalApp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.pawlak.rentalApp.model.Rental;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PenaltyServiceTest {

    private RentalService rentalService;
    private PenaltyService penaltyService;

    @BeforeEach
    public void setUp() {
        rentalService = mock(RentalService.class);
        penaltyService = new PenaltyService(rentalService);
    }

    @Test
    public void shouldApplyPenaltyIfReturnedLate() {
        // given
        LocalDate dueDate = LocalDate.now().minusDays(3);
        LocalDate returnDate = LocalDate.now(); // zwrócono dzisiaj, 3 dni po terminie
        Rental rental = new Rental(1, null, null, LocalDate.now().minusDays(10), dueDate, returnDate, 0);

        // when
        penaltyService.applyPenalty(rental);

        // then
        double expectedPenalty = 10 + 2 * 4.0; // 3 dni po terminie * 4 zł
        assertEquals(expectedPenalty, rental.getPenaltyFee());
        verify(rentalService).updateRental(rental);
    }

    @Test
    public void shouldApplyPenaltyIfNotReturnedAndPastDueDate() {
        // given
        LocalDate dueDate = LocalDate.now().minusDays(2);
        Rental rental = new Rental(1, null, null, LocalDate.now().minusDays(10), dueDate, null, 0);

        // when
        penaltyService.applyPenalty(rental);

        // then
        double expectedPenalty = 10 + 4.0; // 2 dni po terminie * 4 zł
        assertEquals(expectedPenalty, rental.getPenaltyFee());
        verify(rentalService).updateRental(rental);
    }

    @Test
    public void shouldSetPenaltyToZeroIfReturnedOnTime() {
        // given
        LocalDate dueDate = LocalDate.now();
        LocalDate returnDate = LocalDate.now();
        Rental rental = new Rental(1, null, null, LocalDate.now().minusDays(5), dueDate, returnDate, 0);
        rental.setPenaltyFee(10.0); // na początku kara była 10 zł

        // when
        penaltyService.applyPenalty(rental);

        // then
        assertEquals(0.0, rental.getPenaltyFee());
        verify(rentalService).updateRental(rental);
    }

    @Test
    public void shouldSetPenaltyToZeroIfNotLateYet() {
        // given
        LocalDate dueDate = LocalDate.now().plusDays(1);
        Rental rental = new Rental(1, null, null, LocalDate.now().minusDays(5), dueDate, null, 0);
        rental.setPenaltyFee(15.0);

        // when
        penaltyService.applyPenalty(rental);

        // then
        assertEquals(0.0, rental.getPenaltyFee());
        verify(rentalService).updateRental(rental);
    }
}
