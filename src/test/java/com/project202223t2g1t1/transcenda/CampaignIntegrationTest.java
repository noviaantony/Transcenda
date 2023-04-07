package com.project202223t2g1t1.transcenda;

import com.project202223t2g1t1.transcenda.Campaign.Campaign;
import com.project202223t2g1t1.transcenda.Campaign.CampaignRepository;
import com.project202223t2g1t1.transcenda.Campaign.CampaignRequest;
import com.project202223t2g1t1.transcenda.Campaign.CampaignService;
import com.project202223t2g1t1.transcenda.Card.*;
import com.project202223t2g1t1.transcenda.Exception.MerchantCategoryCodeNotFoundException;
import com.project202223t2g1t1.transcenda.MerchantExclusion.MerchantCategoryCodeExclusionRepository;
import com.project202223t2g1t1.transcenda.MerchantExclusion.MerchantCategoryCodeExclusionRequest;
import com.project202223t2g1t1.transcenda.MerchantExclusion.MerchantCategoryCodeExclusionService;
import com.project202223t2g1t1.transcenda.Security.AESEncryptionUtil;
import com.project202223t2g1t1.transcenda.Transaction.Transaction;
import com.project202223t2g1t1.transcenda.Transaction.TransactionRepository;
import com.project202223t2g1t1.transcenda.Transaction.TransactionRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/testdb",
        "spring.datasource.username=testuser",
        "spring.datasource.password=testpassword"
//        "spring.datasource.url=jdbc:postgresql://localhost:5432/transcendadb",
//        "spring.datasource.username=newbieshine",
//        "spring.datasource.password=zhaoxing",
})
public class CampaignIntegrationTest {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CampaignService campaignService;
    @Autowired
    private CardService cardService;
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private AESEncryptionUtil aesEncryptionUtil;
    @Autowired
    private CardRepository cardRepository;

    @BeforeAll
    public void setup() {
        System.setProperty("AWS_SNS_TOPIC_ARN", System.getenv("AWS_SNS_TOPIC_ARN"));
        System.setProperty("AWS_REGION", System.getenv("AWS_REGION"));
        System.setProperty("AWS_SQS_ACCESS_KEY", System.getenv("AWS_SQS_ACCESS_KEY"));
        System.setProperty("AWS_SQS_SECRET_KEY", System.getenv("AWS_SQS_SECRET_KEY"));
        System.setProperty("AWS_SQS_ENDPOINT", System.getenv("AWS_SQS_ENDPOINT"));
        System.setProperty("INIT_VECTOR", System.getenv("INIT_VECTOR"));
        System.setProperty("AES_SECRET_KEY", System.getenv("AES_SECRET_KEY"));


        for (int i = 1; i <= 100; i++) {
            Transaction transaction = new Transaction();
            transaction.setTransactionId((long) i);
            transactionRepository.save(transaction);
        }
    }

    @Test
    @Order(1)
    public void addCampaignForAmazonShoppingSale() {
        // Creating new campaign request for Amazon
        CampaignRequest campaignRequest = new CampaignRequest("Amazon Shopping Sale",
                "Get 15 points per dollar with Amazon, min speend $100", "2023-04-01",
                "2023-04-30", "Amazon", 15.0, RewardType.POINTS, 100.0,
                "scis_shopping");

        // Get card program for scis_shopping
        CardProgram scis_shopping = cardService.getCardProgram("scis_shopping");

        // Creating new campaign for Amazon
        Campaign amazonCampaign = campaignService.createNewCampaign(campaignRequest, scis_shopping);

        // Assert that campaign information is updated accordingly
        assertEquals(campaignRequest.campaignName(), amazonCampaign.getCampaignName());
        assertEquals(campaignRequest.campaignDescription(), amazonCampaign.getCampaignDescription());
        assertEquals(LocalDate.of(2023,4,1), amazonCampaign.getCampaignStartDate());
        assertEquals(LocalDate.of(2023,4,30), amazonCampaign.getCampaignEndDate());
        assertEquals(campaignRequest.merchantName(), amazonCampaign.getMerchantName());
        assertEquals(campaignRequest.campaignRewardRate(), amazonCampaign.getCampaignRewardRate());
        assertEquals(campaignRequest.campaignRewardType(), amazonCampaign.getCampaignRewardType());
        assertEquals(campaignRequest.campaignRequiredAmountSpend(), amazonCampaign.getCampaignRequiredAmountSpend());

        campaignRepository.delete(amazonCampaign);
    }

    @Test
    @Order(2)
    public void addCampaignForAliExpressCashbackSale() {
        CampaignRequest campaignRequest = new CampaignRequest("Aliexpress Cashback Sale",
                "Get 5% cashback with Aliexpress, min spend $150", "2023-04-01",
                "2023-05-31", "AliExpress", 0.05, RewardType.CASHBACK, 150.0,
                "scis_freedom");

        // Get card program for scis_freedom
        CardProgram scis_freedom = cardService.getCardProgram("scis_freedom");

        // Creating new campaign for Amazon
        Campaign aliExpressCampaign = campaignService.createNewCampaign(campaignRequest, scis_freedom);

        // Assert that campaign information is updated accordingly
        assertEquals(campaignRequest.campaignName(), aliExpressCampaign.getCampaignName());
        assertEquals(campaignRequest.campaignDescription(), aliExpressCampaign.getCampaignDescription());
        assertEquals(LocalDate.of(2023,4,1), aliExpressCampaign.getCampaignStartDate());
        assertEquals(LocalDate.of(2023,5,31), aliExpressCampaign.getCampaignEndDate());
        assertEquals(campaignRequest.merchantName(), aliExpressCampaign.getMerchantName());
        assertEquals(campaignRequest.campaignRewardRate(), aliExpressCampaign.getCampaignRewardRate());
        assertEquals(campaignRequest.campaignRewardType(), aliExpressCampaign.getCampaignRewardType());
        assertEquals(campaignRequest.campaignRequiredAmountSpend(), aliExpressCampaign.getCampaignRequiredAmountSpend());

    }

    @Test
    @Order(3)
    public void processCampaignForAliExpressCashbackSaleSuccess() {

        CardProgram cardProgram = cardService.getCardProgram("scis_freedom");

        Card card = new Card("123456789O", cardProgram, "jane.doe@example.com", "1234567890", aesEncryptionUtil);
        cardRepository.save(card);

        // create a new transaction request
        TransactionRequest transactionRequest = new TransactionRequest(
                aesEncryptionUtil.decrypt(card.getCardNumber()),
                "03/04/2023",
                "scis_freedom",
                2000.0,
                "SGD",
                "AliExpress",
                5964 //  AliExpress
        );

        double rewardRate = campaignService.processCampaignTransaction(transactionRequest);

        Campaign campaign = campaignService.getCampaign("AliExpress");

        // Assert that reward rate will be updated to the campaign
        assertEquals(campaign.getCampaignRewardRate(), rewardRate);

    }


}
