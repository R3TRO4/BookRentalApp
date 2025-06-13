package org.pawlak.rentalApp.util;

import org.pawlak.rentalApp.dao.BookDao;
import org.pawlak.rentalApp.dao.RentalDao;
import org.pawlak.rentalApp.dao.UserDao;
import org.pawlak.rentalApp.service.*;

import java.sql.Connection;

public class AppContext {
    public final BookDao bookDao;
    public final UserDao userDao;
    public final RentalDao rentalDao;

    public final BookService bookService;
    public final UserService userService;
    public final LoginService loginService;
    public final RecommendationService recommendationService;
    public final RentalService rentalService;
    public final RegisterService registerService;
    public final RatingService ratingService;
    public final PenaltyService penaltyService;
    public final PenaltyScheduler penaltyScheduler;
    public final StatisticsService statisticsService;

    public AppContext(Connection connection) {
        this.bookDao = new BookDao(connection);
        this.userDao = new UserDao(connection);
        this.rentalDao = new RentalDao(connection, bookDao, userDao);

        this.bookService = new BookService(bookDao);
        this.userService = new UserService(userDao);
        this.loginService = new LoginService(userDao);
        this.recommendationService = new RecommendationService(bookDao);
        this.rentalService = new RentalService(rentalDao, bookDao);
        this.registerService = new RegisterService(userDao);
        this.ratingService = new RatingService(bookDao);
        this.penaltyService = new PenaltyService(rentalService);
        this.penaltyScheduler = new PenaltyScheduler(penaltyService, rentalService);
        this.statisticsService = new StatisticsService(rentalService);
    }
}
