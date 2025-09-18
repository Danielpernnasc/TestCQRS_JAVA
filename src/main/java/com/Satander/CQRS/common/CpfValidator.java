package com.Satander.CQRS.common;

public class CpfValidator {
    public static boolean isValid(String cpf) {
        if (cpf == null)
            return false;
        cpf = cpf.replaceAll("\\D", "");
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1)
            return false;
        int d1 = 0, d2 = 0;
        for (int i = 0; i < 9; i++) {
            int n = cpf.charAt(i) - '0';
            d1 += n * (10 - i);
            d2 += n * (11 - i);
        }
        d1 = (d1 * 10) % 11;
        if (d1 == 10)
            d1 = 0;
        d2 = (d2 + d1 * 2);
        d2 = (d2 * 10) % 11;
        if (d2 == 10)
            d2 = 0;
        return d1 == cpf.charAt(9) - '0' && d2 == cpf.charAt(10) - '0';
    }
}
