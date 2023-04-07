package com.project202223t2g1t1.transcenda.Campaign;

import com.project202223t2g1t1.transcenda.Card.RewardType;

public record CampaignRequest(String campaignName, String campaignDescription, String campaignStartDate,
                              String campaignEndDate, String merchantName, double campaignRewardRate,
                              RewardType campaignRewardType, double campaignRequiredAmountSpend, String cardProgramName) {
}
