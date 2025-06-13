package org.pawlak.rentalApp.util;

import org.pawlak.rentalApp.service.PenaltyService;
import org.pawlak.rentalApp.service.RentalService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PenaltyScheduler {

    private final PenaltyService penaltyService;
    private final RentalService rentalService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public PenaltyScheduler(PenaltyService penaltyService, RentalService rentalService) {
        this.penaltyService = penaltyService;
        this.rentalService = rentalService;
    }

    public void start() {
        Runnable task = () -> {
            System.out.println("Sprawdzam kary... " + java.time.LocalDateTime.now());
            rentalService.getAllRentals().forEach(penaltyService::applyPenalty);
        };

        // Uruchom pierwsze zadanie natychmiast, potem powtarzaj co 24 godziny
        scheduler.scheduleAtFixedRate(task, 0, 24, TimeUnit.HOURS);
    }

    public void stop() {
        scheduler.shutdown();
    }
}
