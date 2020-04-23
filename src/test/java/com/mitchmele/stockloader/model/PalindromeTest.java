package com.mitchmele.stockloader.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PalindromeTest {

    Palindrome subject;

    @BeforeEach
    void setUp() {
        subject = new Palindrome();
    }

    @Test
    public void isPalindrome_shouldReturnYesIfTrue() {
        String inputString = "madam";

        String actual = subject.isPalindrome(inputString);
        String expected = "Yes";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void isPalindrome_shouldReturnNoIfFalse() {
        String inputString = "test";

        String actual = subject.isPalindrome(inputString);
        String expected = "No";

        assertThat(actual).isEqualTo(expected);
    }
}