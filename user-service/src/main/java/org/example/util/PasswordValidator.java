package org.example.util;

import java.util.regex.Pattern;

public class PasswordValidator {

    // Regular expression for password validation
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public static boolean validate(final String password) {
        return pattern.matcher(password).matches();
    }
}