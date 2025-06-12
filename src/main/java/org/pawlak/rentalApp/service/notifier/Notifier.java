package org.pawlak.rentalApp.service.notifier;

import org.pawlak.rentalApp.model.User;

public interface Notifier {
    void notify(User user, String message);
}
