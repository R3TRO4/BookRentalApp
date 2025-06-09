package org.pawlak.rentalApp.util;

import org.mindrot.jbcrypt.BCrypt;

public class HashGenerator {
    public static void main(String[] args) {
        String plainPassword = "AdminTempPass123";
        String hashed = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        System.out.println("Hashed password: " + hashed);
    }
}