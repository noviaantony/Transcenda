package com.project202223t2g1t1.transcenda.Campaign;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project202223t2g1t1.transcenda.Card.CardProgram;
import com.project202223t2g1t1.transcenda.Card.RewardType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Campaign {
    @Id
    @SequenceGenerator(
            name = "campaign_seq",
            sequenceName = "campaign_seq"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "campaign_seq"
    )
    private Long campaignId;
    private String campaignName;
    private String campaignDescription;
    private LocalDate campaignStartDate;
    private LocalDate campaignEndDate;
    private String merchantName; // affiliated merchant
    private double campaignRewardRate;
    private RewardType campaignRewardType; // points, mile or cashback
    private double campaignRequiredAmountSpend; // e.g. min spend 100
    private String cardProgramName;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "cardProgram", nullable = false)
    private CardProgram cardProgram;

    public Campaign(String campaignName, String campaignDescription, LocalDate campaignStartDate,
                    LocalDate campaignEndDate, String merchantName, double campaignRewardRate,
                    RewardType campaignRewardType, double campaignRequiredAmountSpend,String cardProgramName, CardProgram cardProgram) {
        this.campaignName = campaignName;
        this.campaignDescription = campaignDescription;
        this.campaignStartDate = campaignStartDate;
        this.campaignEndDate = campaignEndDate;
        this.merchantName = merchantName;
        this.campaignRewardRate = campaignRewardRate;
        this.campaignRewardType = campaignRewardType;
        this.campaignRequiredAmountSpend = campaignRequiredAmountSpend;
        this.cardProgramName = cardProgramName;
        this.cardProgram = cardProgram;

    }
}
