package com.mg.shineglass.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class validation {

    public static boolean validateFields(String name){

        return !TextUtils.isEmpty(name);
    }

    public static boolean validateEmail(String string) {

        return !TextUtils.isEmpty(string) && Patterns.EMAIL_ADDRESS.matcher(string).matches();
    }

    public  static boolean validatePassword(String p1, String p2)
    {
        return !p1.equals(p2);
    }


    public static boolean validatePhone(String s)
    {
        // The given argument to compile() method
        // is regular expression. With the help of
        // regular expression we can validate mobile
        // number.
        // 1) Begins with 0 or 91
        // 2) Then contains 7 or 8 or 9.
        // 3) Then contains 9 digits

        Pattern p = Pattern.compile("(0/91)?[6-9][0-9]{9}");

        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }

    public static boolean VALIDATE_PASSWORD(String s)

    {String regex = "^(?=.*[0-9])"
            + "(?=.*[a-z])(?=.*[A-Z])"
            + "(?=.*[@#$%^&+=])"
            + "(?=\\S+$).{6,20}$";
        Pattern p=Pattern.compile(regex);
        Matcher m=p.matcher(s);
        return (m.find() && m.group().equals(s));

    }


}