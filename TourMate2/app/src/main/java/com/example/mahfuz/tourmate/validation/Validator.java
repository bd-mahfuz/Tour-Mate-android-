package com.example.mahfuz.tourmate.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public static boolean emailValidator(String email) {
        Matcher matcher = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(email);
        if(matcher.find())
        {
            return true;
        }
        return false;
    }

    public static boolean passwordValidator(String password) {
        if (password.length() >= 6) {
            return true;
        }
        return false;
    }

    public static boolean nameValidator(String name) {
        if(name.length() >= 6) {
            return true;
        }
        return false;
    }

    public static boolean phoneValidator(String phone) {
        if (phone != null || phone.isEmpty() == false) {
            return true;
        }
        return false;
    }



}
