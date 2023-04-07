package com.project202223t2g1t1.transcenda;

import com.project202223t2g1t1.transcenda.Card.*;
import com.project202223t2g1t1.transcenda.Exception.MerchantCategoryCodeNotFoundException;
import com.project202223t2g1t1.transcenda.MerchantExclusion.MerchantCategoryCodeExclusion;
import com.project202223t2g1t1.transcenda.MerchantExclusion.MerchantCategoryCodeExclusionRepository;
import com.project202223t2g1t1.transcenda.MerchantExclusion.MerchantCategoryCodeExclusionRequest;
import com.project202223t2g1t1.transcenda.MerchantExclusion.MerchantCategoryCodeExclusionService;
import com.project202223t2g1t1.transcenda.Security.AESEncryptionUtil;
import com.project202223t2g1t1.transcenda.Transaction.Transaction;
import com.project202223t2g1t1.transcenda.Transaction.TransactionRepository;
import com.project202223t2g1t1.transcenda.Transaction.TransactionRequest;
import com.project202223t2g1t1.transcenda.Transaction.TransactionService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
public class MccIntegrationTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MerchantCategoryCodeExclusionService merchantCategoryCodeExclusionService;

    @Autowired
    private MerchantCategoryCodeExclusionRepository merchantCategoryCodeExclusionRepository;



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
    public void addExclusionForMCC3075() throws MerchantCategoryCodeNotFoundException {
        // Adding SIA (3075) to Exclusion
        MerchantCategoryCodeExclusionRequest exclusionRequest = new MerchantCategoryCodeExclusionRequest(3075,"Airlines", "Singapore Airlines");

        String success = merchantCategoryCodeExclusionService.addMerchantCategoryCodeExclusion(exclusionRequest);

        // Assert that the MCC was added correctly
        assertEquals("Merchant Category Code: 3075 exclusion added", success);
        // Assert that transaction eligibility returns false
        assertEquals(false, merchantCategoryCodeExclusionService.checkTransactionEligibility(3075));

        merchantCategoryCodeExclusionRepository.delete(merchantCategoryCodeExclusionService.getMerchantCategoryCode(3075));

    }

    @Test
    @Order(2)
    public void testExclusionForMCC5309() throws MerchantCategoryCodeNotFoundException {
        // Adding Duty Free Store (5309) to Exclusion
        MerchantCategoryCodeExclusionRequest exclusionRequest = new MerchantCategoryCodeExclusionRequest(5309,"Retail", "Duty Free Stores");

        String success = merchantCategoryCodeExclusionService.addMerchantCategoryCodeExclusion(exclusionRequest);

        // Assert that the MCC was added correctly
        assertEquals("Merchant Category Code: 5309 exclusion added", success);
        // Assert that transaction eligibility returns false
        assertEquals(false, merchantCategoryCodeExclusionService.checkTransactionEligibility(5309));

        merchantCategoryCodeExclusionRepository.delete(merchantCategoryCodeExclusionService.getMerchantCategoryCode(5309));
    }

    @Test
    @Order(3)
    public void testExclusionForMCC8062() throws MerchantCategoryCodeNotFoundException {
        // Adding Hospitals (8062) to Exclusion
        MerchantCategoryCodeExclusionRequest exclusionRequest = new MerchantCategoryCodeExclusionRequest(8062,"Professional", "Hospitals");

        String success = merchantCategoryCodeExclusionService.addMerchantCategoryCodeExclusion(exclusionRequest);

        // Assert that the MCC was added correctly
        assertEquals("Merchant Category Code: 8062 exclusion added", success);
        // Assert that transaction eligibility returns false
        assertEquals(false, merchantCategoryCodeExclusionService.checkTransactionEligibility(8062));

        merchantCategoryCodeExclusionRepository.delete(merchantCategoryCodeExclusionService.getMerchantCategoryCode(8062));

    }

    @Test
    @Order(4)
    public void getExclusionForMCC6051() throws MerchantCategoryCodeNotFoundException {

        MerchantCategoryCodeExclusion merchantCategoryCodeExclusion = merchantCategoryCodeExclusionService.getMerchantCategoryCode(6051);

        assertEquals(6051, merchantCategoryCodeExclusion.getMerchantCategoryCode());

    }

    @Test
    @Order(5)
    public void getExclusionForMCC9399() throws MerchantCategoryCodeNotFoundException {

        MerchantCategoryCodeExclusion merchantCategoryCodeExclusion = merchantCategoryCodeExclusionService.getMerchantCategoryCode(9399);

        assertEquals(9399, merchantCategoryCodeExclusion.getMerchantCategoryCode());

    }

}
