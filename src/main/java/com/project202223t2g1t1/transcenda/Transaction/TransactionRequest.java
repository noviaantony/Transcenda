package com.project202223t2g1t1.transcenda.Transaction;


import java.time.LocalDate;

public record TransactionRequest(String cardNumber, String transactionDate, String cardProgramType, double transactionAmount,
                                 String transactionCurrency, String merchantName, Integer merchantCategoryCode) {

}
