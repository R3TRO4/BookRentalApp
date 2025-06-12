package org.pawlak.rentalApp.service.notifier;

import org.pawlak.rentalApp.model.User;

public class ConsoleNotifier implements Notifier {
    @Override
    public void notify(User user, String message) {
        System.out.println("Powiadomienie dla " + user.getName() + ": " + message);
    }
}
