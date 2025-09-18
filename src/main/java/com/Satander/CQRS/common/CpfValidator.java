package com.Satander.CQRS.common;

public class CpfValidator {

    public static boolean isValid(String cpf) {
        if (cpf == null || cpf.length() != 11 || !cpf.matches("\\d+")) {
            return false;
        }
        // Verifica se todos os dígitos são iguais (CPF inválido)
        if (cpf.chars().allMatch(c -> c == cpf.charAt(0))) {
            return false;
        }
        // Lógica de validação dos dígitos verificadores
        try {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += (cpf.charAt(i) - '0') * (10 - i);
            }
            int d1 = 11 - (sum % 11);
            if (d1 >= 10)
                d1 = 0;

            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += (cpf.charAt(i) - '0') * (11 - i);
            }
            int d2 = 11 - (sum % 11);
            if (d2 >= 10)
                d2 = 0;

            return (cpf.charAt(9) - '0') == d1 && (cpf.charAt(10) - '0') == d2;
        } catch (Exception e) {
            return false;
        }
    }
}