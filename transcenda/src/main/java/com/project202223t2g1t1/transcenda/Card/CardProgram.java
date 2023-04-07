package com.project202223t2g1t1.transcenda.Card;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.project202223t2g1t1.transcenda.Campaign.Campaign;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "cardProgram")
public class CardProgram {
    @Id
    private String cardProgram;
    private String cardProgramDescription;
    private RewardType cardRewardType; // points, miles cashback

    @OneToMany(mappedBy = "cardProgram", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<CardProgramRewardRate> cardProgramRewardRate;

    @OneToMany(mappedBy = "cardProgram", fetch = FetchType.EAGER)
    @JsonManagedReference
    @JsonIgnore
    private List<Card> card;

    @OneToMany(mappedBy = "cardProgram",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Campaign> campaign;

    public CardProgram(String cardProgram, String cardProgramDescription, RewardType cardRewardType) {
        this.cardProgram = cardProgram;
        this.cardProgramDescription = cardProgramDescription;
        this.cardRewardType = cardRewardType;
    }
}
