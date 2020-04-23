package com.mitchmele.stockloader.model;

import java.util.Arrays;

public class Palindrome {

    public String isPalindrome(String inputString) {
        String reversedString = createReversedString(inputString);
        return reversedString.equals(inputString.toLowerCase()) ? "Yes" : "No";
    }

    public String createReversedString(String input) {
        String[] letters = input.split("");
        StringBuilder reversedString = new StringBuilder();
        for (int i = letters.length - 1 ; i >= 0; i--) {
            reversedString.append(letters[i]);
        }
        return reversedString.toString().toLowerCase();
    }
}

/*
* public String reverseString(String str) {
    String res = "";
    for (int i = 0; i < str.length(); i++) {
       res = str.charAt(i) + res;            // Add each char to the *front*
    }
    return res;
}
* */