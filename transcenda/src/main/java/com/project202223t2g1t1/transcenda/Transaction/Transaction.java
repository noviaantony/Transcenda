package com.project202223t2g1t1.transcenda.Transaction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project202223t2g1t1.transcenda.Card.Card;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @SequenceGenerator(
            name = "transaction_sequence",
            sequenceName = "transaction_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "transaction_sequence"
    )
    private Long transactionId;
    private String userId; // retrieve userId from the card used
    private LocalDate transactionDate;
    private double transactionAmount;
    private Double transactionRewardEarned;
    private double transactionRewardBalance; //reward balance after transaction
    private String transactionCurrency;
    private String cardProgramType;
    private String merchantName;
    private Integer merchantCategoryCode;
    private String remarks;
    private LocalDateTime createdAt;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "card_id")
    private Card card;

    public Transaction(LocalDate transactionDate, String userId, double transactionAmount,Double transactionRewardEarned, double transactionRewardBalance, String transactionCurrency, String cardProgramType,
                       String merchantName, String remarks, Integer merchantCategoryCode, Card card) {
        this.transactionDate = transactionDate;
        this.userId = userId;
        this.transactionAmount = transactionAmount;
        this.transactionRewardEarned = transactionRewardEarned;
        this.transactionRewardBalance = transactionRewardBalance;
        this.transactionCurrency = transactionCurrency;
        this.cardProgramType = cardProgramType;
        this.merchantName = merchantName;
        this.remarks = remarks;
        this.merchantCategoryCode = merchantCategoryCode;
        this.card = card;
        this.createdAt = LocalDateTime.now( ZoneId.of("Asia/Singapore"));
    }
}
