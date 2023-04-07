package com.project202223t2g1t1.transcenda.Card;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardProgramRewardRate {
    @Id
    @SequenceGenerator(
            name = "card_program_reward_rate_sequence",
            sequenceName = "card_program_reward_rate_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "card_program_reward_rate_sequence"
    )
    private Long cardProgramRewardRateId;
    private Double earnRate;
    private MerchantCategory earnRateMerchantCategory;

    @ManyToOne
    @JoinColumn(name = "card_program", nullable = false)
    @JsonBackReference
    private CardProgram cardProgram;

    public CardProgramRewardRate(Double earnRate, MerchantCategory earnRateMerchantCategory, CardProgram cardProgram) {
        this.earnRate = earnRate;
        this.earnRateMerchantCategory = earnRateMerchantCategory;
        this.cardProgram = cardProgram;
    }
}
