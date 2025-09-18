package com.Satander.CQRS.model;

import java.math.BigDecimal;

public record TransactionItem(String type, BigDecimal valor, String data) {

}
