package com.project202223t2g1t1.transcenda.Card;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project202223t2g1t1.transcenda.Security.AESEncryptionUtil;
import com.project202223t2g1t1.transcenda.Transaction.Transaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Card {

    @Id
    @SequenceGenerator(
            name = "card_sequence",
            sequenceName = "card_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "card_sequence"
    )
    private Long cardId;
    private String cardNumber; // encode before storing

    private double rewardEarned; // total reward earned by card
    private String userEmail; //take from AWS cognito
    private String userContactNumber; //take from AWS cognito

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIdentityReference
    @JoinColumn(name = "card_program", nullable = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CardProgram cardProgram;

    @OneToMany(mappedBy = "card")
    @JsonManagedReference
    private List<Transaction> transaction;

    //getter and setter for cardProgram
    public CardProgram getCardProgram() {
        return cardProgram;
    }
    public void setCardProgram(CardProgram cardProgram) {
        this.cardProgram = cardProgram;
    }

    public Card(String cardNumber, CardProgram cardProgram, String userEmail, String userContactNumber, AESEncryptionUtil aesEncryptionUtil) {
        this.cardNumber = aesEncryptionUtil.encrypt(cardNumber);
        this.cardProgram = cardProgram;
        this.userEmail = userEmail;
        this.rewardEarned = 0;
        this.userContactNumber = userContactNumber;
    }
}
