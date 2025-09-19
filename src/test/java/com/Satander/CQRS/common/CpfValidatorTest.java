package com.Satander.CQRS.common;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CpfValidatorTest {
    @Test
    void aceitaCpfsValidos() {
        assertThat(CpfValidator.isValid("14587080047")).isTrue();
        assertThat(CpfValidator.isValid("37009879605")).isTrue();
    }

    @Test
    void rejeitaCpfsInvalidos() {
        assertThat(CpfValidator.isValid("00000000000")).isFalse();
        assertThat(CpfValidator.isValid("12345678900")).isFalse();
        assertThat(CpfValidator.isValid("abc")).isFalse();
        assertThat(CpfValidator.isValid("145.870.800-47")).isTrue(); // se seu validador normaliza pontuação
    }
}
