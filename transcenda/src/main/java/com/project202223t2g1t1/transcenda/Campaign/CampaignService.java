package com.project202223t2g1t1.transcenda.Campaign;


import com.project202223t2g1t1.transcenda.Card.CardProgram;
import com.project202223t2g1t1.transcenda.Card.CardService;
import com.project202223t2g1t1.transcenda.Transaction.TransactionRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import software.amazon.awssdk.services.sqs.endpoints.internal.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.StringTokenizer;

@Service
public class CampaignService {
    private final CampaignRepository campaignRepository;

    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    public double processCampaignTransaction(TransactionRequest transaction) {
        //check if transaction is eligible for campaign using merchant name
        //if eligible, return campaign amount
        double amountSpent = transaction.transactionAmount();
        if (!transaction.transactionCurrency().equals("SGD")) {
            amountSpent = amountSpent * 1.35;
        }

        StringTokenizer st = new StringTokenizer(transaction.transactionDate(), "/");
        int day = Integer.parseInt((String) st.nextElement());
        int month = Integer.parseInt((String) st.nextElement());
        int year = Integer.parseInt((String) st.nextElement());

        LocalDate date = LocalDate.of(year, month, day);

        Campaign campaign = campaignRepository.findByMerchantName(transaction.merchantName());
        //check if campaign exists or is active based on the date of the transaction
        if (campaign == null || !(date.isAfter(campaign.getCampaignStartDate()) && date.isBefore(campaign.getCampaignEndDate()))) {
            return 0;
        }

        //check if transaction amount is eligible for campaign
        if (amountSpent >= campaign.getCampaignRequiredAmountSpend()) {
            return campaign.getCampaignRewardRate();
        }
        return 0;
    }

    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    public Campaign getCampaign(String merchantName){
        return campaignRepository.findByMerchantName(merchantName);
    };

    public Campaign createNewCampaign(CampaignRequest campaignRequest, CardProgram cardProgram ) {

        //Create new campaign
        Campaign campaign = new Campaign(
                campaignRequest.campaignName(),
                campaignRequest.campaignDescription(),
                LocalDate.parse(campaignRequest.campaignStartDate()),
                LocalDate.parse( campaignRequest.campaignEndDate()),
                campaignRequest.merchantName(),
                campaignRequest.campaignRewardRate(),
                campaignRequest.campaignRewardType(),
                campaignRequest.campaignRequiredAmountSpend(),
                campaignRequest.cardProgramName(),
                cardProgram
        );
        return campaignRepository.save(campaign);
    }

    public String deleteCampaign(Long campaignId) {
        //check if campaign exists
        if (!campaignRepository.existsById(campaignId)) {
            return "Campaign does not exist";
        }

        campaignRepository.deleteById(campaignId);
        return "Campaign deleted successfully";
    }
}
