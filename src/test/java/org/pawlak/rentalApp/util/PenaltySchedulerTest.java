package org.pawlak.rentalApp.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pawlak.rentalApp.model.Rental;
import org.pawlak.rentalApp.service.PenaltyService;
import org.pawlak.rentalApp.service.RentalService;
import org.pawlak.rentalApp.util.PenaltyScheduler;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class PenaltySchedulerTest {

    private PenaltyService penaltyService;
    private RentalService rentalService;
    private PenaltyScheduler penaltyScheduler;

    @BeforeEach
    public void setUp() {
        penaltyService = mock(PenaltyService.class);
        rentalService = mock(RentalService.class);
        penaltyScheduler = new PenaltyScheduler(penaltyService, rentalService);
    }

    @AfterEach
    public void tearDown() {
        penaltyScheduler.stop();
    }

    @Test
    public void TC_084_shouldStartSchedulerAndApplyPenalty() throws InterruptedException {
        // given
        Rental rental = mock(Rental.class);
        when(rentalService.getAllRentals()).thenReturn(List.of(rental));

        // when
        penaltyScheduler.start();

        TimeUnit.MILLISECONDS.sleep(200);

        // then
        verify(penaltyService, atLeastOnce()).applyPenalty(rental);
    }
}
