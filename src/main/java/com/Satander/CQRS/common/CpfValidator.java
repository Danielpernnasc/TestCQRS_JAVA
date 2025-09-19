package com.Satander.CQRS.common;

public final class CpfValidator {
    private CpfValidator() {
    }

    public static boolean isValid(String cpf) {
        if (cpf == null)
            return false;
        cpf = cpf.replaceAll("\\D", "");
        if (cpf.length() != 11)
            return false;
        if (cpf.matches("(\\d)\\1{10}"))
            return false;
        int d1 = 0, d2 = 0;
        for (int i = 0; i < 9; i++) {
            int x = cpf.charAt(i) - '0';
            d1 += x * (10 - i);
            d2 += x * (11 - i);
        }
        d1 = 11 - (d1 % 11);
        d1 = (d1 >= 10) ? 0 : d1;
        d2 += d1 * 2;
        d2 = 11 - (d2 % 11);
        d2 = (d2 >= 10) ? 0 : d2;
        return (cpf.charAt(9) - '0' == d1) && (cpf.charAt(10) - '0' == d2);
    }
}
