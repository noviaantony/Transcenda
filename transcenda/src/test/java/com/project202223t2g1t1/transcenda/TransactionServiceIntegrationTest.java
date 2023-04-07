package com.project202223t2g1t1.transcenda;

import com.project202223t2g1t1.transcenda.Card.*;
import com.project202223t2g1t1.transcenda.MerchantExclusion.MerchantCategoryCodeExclusionService;
import com.project202223t2g1t1.transcenda.Security.AESEncryptionUtil;
import com.project202223t2g1t1.transcenda.Transaction.Transaction;
import com.project202223t2g1t1.transcenda.Transaction.TransactionRepository;
import com.project202223t2g1t1.transcenda.Transaction.TransactionRequest;
import com.project202223t2g1t1.transcenda.Transaction.TransactionService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.text.ParseException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


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
public class TransactionServiceIntegrationTest {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CardService cardService;

    @Autowired
    private CardProgramRepository cardProgramRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AESEncryptionUtil aesEncryptionUtil;

    @Autowired
    private CardProgramRewardRateRepository cardProgramRewardRateRepository;
    @Autowired
    private MerchantCategoryCodeExclusionService merchantCategoryCodeExclusionService;



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
    public void testGetRecent50Transactions() {
        // Act
        List<Transaction> recentTransactions = transactionService.getRecent50Transactions();

        // Assert
        assertEquals(50, recentTransactions.size());
        assertEquals(100, Math.toIntExact(recentTransactions.get(0).getTransactionId()));
        assertEquals(51, Math.toIntExact(recentTransactions.get(49).getTransactionId()));
    }

    @Test
    @Order(2)
    public void testGetAllTransactions() {
        // Act
        List<Transaction> allTransactions = transactionService.getAllTransactions();

        // Assert
        assertEquals(100, allTransactions.size());
        assertEquals(1, Math.toIntExact(allTransactions.get(0).getTransactionId()));
        assertEquals(100, Math.toIntExact(allTransactions.get(99).getTransactionId()));
    }

    //test for add successful transaction
    @Test
    @Order(3)
    void testAddTransaction_Success() throws ParseException {
        //create test card program
        CardProgram cardProgram = new CardProgram();
        cardProgram.setCardProgram("scis_shopping");
        cardProgram.setCardProgramDescription("Shopping Card Program");
        cardProgram.setCardRewardType(RewardType.POINTS);

        cardProgramRepository.save(cardProgram);

//        // Create a new card program reward rate
//        CardProgramRewardRate rate1 = new CardProgramRewardRate();
//        rate1.setEarnRate(1.5);
//        rate1.setEarnRateMerchantCategory(MerchantCategory.OTHER);
//        rate1.setCardProgram(cardProgram);
//
//        CardProgramRewardRate rate2 = new CardProgramRewardRate();
//        rate2.setEarnRate(2.0);
//        rate2.setEarnRateMerchantCategory(MerchantCategory.SHOPPING);
//        rate2.setCardProgram(cardProgram);
//
//        CardProgramRewardRate rate3 = new CardProgramRewardRate();
//        rate3.setEarnRate(3.0);
//        rate3.setEarnRateMerchantCategory(MerchantCategory.ONLINE_SHOPPING);
//        rate3.setCardProgram(cardProgram);


        // Create a new card
        Card card = new Card("1A", cardProgram, "john1.doe@example.com", "1234567890", aesEncryptionUtil);

        cardRepository.save(card);

        // Create a new transaction request
        TransactionRequest transactionRequest = new TransactionRequest(
                aesEncryptionUtil.decrypt(card.getCardNumber()),
                "01/01/2022",
                "scis_shopping",
                100.0,
                "USD",
                "Test Merchant",
                1234
        );

        Transaction createdTransaction = transactionService.addTransaction(transactionRequest);

        // Assert
        assertNotNull(createdTransaction);
        assertEquals(transactionRequest.transactionAmount(), createdTransaction.getTransactionAmount());
        assertEquals(transactionRequest.transactionCurrency(), createdTransaction.getTransactionCurrency());
        assertEquals(transactionRequest.merchantName(), createdTransaction.getMerchantName());
        assertEquals(transactionRequest.merchantCategoryCode(), createdTransaction.getMerchantCategoryCode());

    }

    @Test
    @Order(4)
    public void testAddTransactionInvalidCardNumber() throws ParseException {
        // create a new card program
        CardProgram cardProgram = new CardProgram();
        cardProgram.setCardProgram("scis_shopping");
        cardProgram.setCardProgramDescription("Shopping Card Program");
        cardProgram.setCardRewardType(RewardType.POINTS);
        cardProgramRepository.save(cardProgram);

        // create a new card
        Card card = new Card("12B", cardProgram, "john2.doe@example.com", "1234567890", aesEncryptionUtil);
        cardRepository.save(card);

        // create a new transaction request with an invalid card number
        TransactionRequest transactionRequest = new TransactionRequest(
                "invalid_card_number",
                "01/01/2022",
                "scis_shopping",
                100.0,
                "USD",
                "Test Merchant",
                1234
        );

        // call the addTransaction method and expect a Null pointer exception  to be thrown
        Throwable exception = assertThrows(NullPointerException.class, () -> {
            transactionService.addTransaction(transactionRequest);
        });

        // assert that the exception message contains the expected error message
        assertTrue(exception.getMessage().contains("Card does not exist in the system"));

    }

    @Test
    @Order(5)
    public void testAddTransactionScisShopping() throws ParseException {
        // create a new card program
        CardProgram cardProgram = new CardProgram();
        cardProgram.setCardProgram("scis_shopping");
        cardProgram.setCardProgramDescription("Shopping Card Program");
        cardProgram.setCardRewardType(RewardType.POINTS);

        cardProgramRepository.save(cardProgram);
        // amount of reward for the program is created by the application config file

        // create a new card
        Card card = new Card("123C", cardProgram, "john3.doe@example.com", "1234567890", aesEncryptionUtil);
        cardRepository.save(card);

        // create a new transaction request
        TransactionRequest transactionRequest = new TransactionRequest(
                aesEncryptionUtil.decrypt(card.getCardNumber()),
                "01/01/2022",
                "scis_shopping",
                100.0,
                "SGD",
                "Test Merchant",
                5001 //  between 5000 and 5999 for shopping
        );

        // call the addTransaction method
        Transaction createdTransaction = transactionService.addTransaction(transactionRequest);

        // assert that the transaction was created successfully
        assertNotNull(createdTransaction);
        assertEquals(transactionRequest.transactionAmount(), createdTransaction.getTransactionAmount());
        assertEquals(transactionRequest.transactionCurrency(), createdTransaction.getTransactionCurrency());
        assertEquals(transactionRequest.merchantName(), createdTransaction.getMerchantName());
        assertEquals(transactionRequest.merchantCategoryCode(), createdTransaction.getMerchantCategoryCode());

        // assert that the reward balance was updated correctly
        Double expectedRewardBalance = 400.0; // 4 points earned per dollar spent
        assertEquals(expectedRewardBalance, createdTransaction.getTransactionRewardBalance());

    }

    @Test
    @Order(6)
    public void testAddTransactionScisPremiumMiles() throws ParseException {
        // create a new card program
        CardProgram cardProgram = new CardProgram();
        cardProgram.setCardProgram("scis_premiummiles");
        cardProgram.setCardProgramDescription("Premium Miles Card Program");
        cardProgram.setCardRewardType(RewardType.MILES);
        cardProgramRepository.save(cardProgram);

        // create a new card
        Card card = new Card("1234D", cardProgram, "john4.doe@example.com", "1234567890", aesEncryptionUtil);
        cardRepository.save(card);

        // create a new transaction request
        TransactionRequest transactionRequest = new TransactionRequest(
                aesEncryptionUtil.decrypt(card.getCardNumber()),
                "01/01/2022",
                "scis_premiummiles",
                100.0,
                "SGD",
                "Test Merchant",
                3600 //  between 3500 and 3999 for hotel
        );

        // call the addTransaction method
        Transaction createdTransaction = transactionService.addTransaction(transactionRequest);

        // assert that the transaction was created successfully
        assertNotNull(createdTransaction);
        assertEquals(transactionRequest.transactionAmount(), createdTransaction.getTransactionAmount());
        assertEquals(transactionRequest.transactionCurrency(), createdTransaction.getTransactionCurrency());
        assertEquals(transactionRequest.merchantName(), createdTransaction.getMerchantName());
        assertEquals(transactionRequest.merchantCategoryCode(), createdTransaction.getMerchantCategoryCode());

        // assert that the reward balance was updated correctly
        Double expectedRewardBalance = 100 * (1.1 + 3); // (1.1 + 3) miles earned per dollar spent as 1.1 base rate  + 3 bonus rate (hotel)
        assertEquals(expectedRewardBalance, createdTransaction.getTransactionRewardBalance());


    }


    @Test
    @Order(7)
    public void testAddTransactionScisPlatinumMiles() throws ParseException {
        // create a new card program
        CardProgram cardProgram = new CardProgram();
        cardProgram.setCardProgram("scis_platinummiles");
        cardProgram.setCardProgramDescription("Platinum Miles Card Program");
        cardProgram.setCardRewardType(RewardType.MILES);
        cardProgramRepository.save(cardProgram);

        // create a new card
        Card card = new Card("12345E", cardProgram, "john5.doe@example.com", "1234567890", aesEncryptionUtil);
        cardRepository.save(card);

        // create a new transaction request
        TransactionRequest transactionRequest = new TransactionRequest(
                aesEncryptionUtil.decrypt(card.getCardNumber()),
                "01/01/2022",
                "scis_platinummiles",
                100.0,
                "SGD",
                "Test Merchant",
                3600 //  between 3500 and 3999 for hotel
        );

        // call the addTransaction method
        Transaction createdTransaction = transactionService.addTransaction(transactionRequest);

        // assert that the transaction was created successfully
        assertNotNull(createdTransaction);
        assertEquals(transactionRequest.transactionAmount(), createdTransaction.getTransactionAmount());
        assertEquals(transactionRequest.transactionCurrency(), createdTransaction.getTransactionCurrency());
        assertEquals(transactionRequest.merchantName(), createdTransaction.getMerchantName());
        assertEquals(transactionRequest.merchantCategoryCode(), createdTransaction.getMerchantCategoryCode());

        // assert that the reward balance was updated correctly
        Double expectedRewardBalance = 100 * (1.4 + 3); // (1.4 + 3) miles earned per dollar spent as 1.4 base rate  + 3 bonus rate (hotel)
        assertEquals(expectedRewardBalance, createdTransaction.getTransactionRewardBalance());


    }

    @Test
    @Order(8)
    public void testAddTransactionScisFreedomMiles() throws ParseException {
        // create a new card program
        CardProgram cardProgram = new CardProgram();
        cardProgram.setCardProgram("scis_freedom");
        cardProgram.setCardProgramDescription("Platinum Miles Card Program");
        cardProgram.setCardRewardType(RewardType.MILES);
        cardProgramRepository.save(cardProgram);

        // create a new card
        Card card = new Card("123456G", cardProgram, "john6.doe@example.com", "1234567890", aesEncryptionUtil);
        cardRepository.save(card);

        // create a new transaction request
        TransactionRequest transactionRequest = new TransactionRequest(
                aesEncryptionUtil.decrypt(card.getCardNumber()),
                "01/01/2022",
                "scis_freedom",
                1500.0,
                "SGD",
                "Test Merchant",
                1234 //  between 3500 and 3999 for hotel
        );

        // call the addTransaction method
        Transaction createdTransaction = transactionService.addTransaction(transactionRequest);

        // assert that the transaction was created successfully
        assertNotNull(createdTransaction);
        assertEquals(transactionRequest.transactionAmount(), createdTransaction.getTransactionAmount());
        assertEquals(transactionRequest.transactionCurrency(), createdTransaction.getTransactionCurrency());
        assertEquals(transactionRequest.merchantName(), createdTransaction.getMerchantName());
        assertEquals(transactionRequest.merchantCategoryCode(), createdTransaction.getMerchantCategoryCode());

        // assert that the reward balance was updated correctly
        Double expectedRewardBalance = 1500 * 0.01; // (1.4 + 3) miles earned per dollar spent as 1.4 base rate  + 3 bonus rate (hotel)
        assertEquals(expectedRewardBalance, createdTransaction.getTransactionRewardBalance());


    }

    @Test
    @Order(9)
    public void testExclusionForMCC6051() throws ParseException {
        // create a new card program
        CardProgram cardProgram = new CardProgram();
        cardProgram.setCardProgram("scis_freedom");
        cardProgram.setCardProgramDescription("Platinum Miles Card Program");
        cardProgram.setCardRewardType(RewardType.MILES);
        cardProgramRepository.save(cardProgram);

        // create a new card
        Card card = new Card("1234567G", cardProgram, "john7.doe@example.com", "1234567890", aesEncryptionUtil);
        cardRepository.save(card);

        // create a new transaction request
        TransactionRequest transactionRequest = new TransactionRequest(
                aesEncryptionUtil.decrypt(card.getCardNumber()),
                "01/01/2022",
                "scis_freedom",
                1500.0,
                "SGD",
                "Test Merchant",
                6051 //  Exclusion includes: 6051,9399,6540
        );

        // Check if MCC is part of the exclusion
        assertEquals(false, merchantCategoryCodeExclusionService.checkTransactionEligibility(
                transactionRequest.merchantCategoryCode()));

        // call the addTransaction method
        Transaction createdTransaction = transactionService.addTransaction(transactionRequest);

        // assert that the transaction was created successfully
        assertNotNull(createdTransaction);
        assertEquals(transactionRequest.transactionAmount(), createdTransaction.getTransactionAmount());
        assertEquals(transactionRequest.transactionCurrency(), createdTransaction.getTransactionCurrency());
        assertEquals(transactionRequest.merchantName(), createdTransaction.getMerchantName());
        assertEquals(transactionRequest.merchantCategoryCode(), createdTransaction.getMerchantCategoryCode());

        // assert that the reward earned is $0.0
        assertEquals(0.0, createdTransaction.getTransactionRewardEarned());

    }

    @Test
    @Order(10)
    public void testExclusionForMCC9399() throws ParseException {
        // create a new card program
        CardProgram cardProgram = new CardProgram();
        cardProgram.setCardProgram("scis_freedom");
        cardProgram.setCardProgramDescription("Platinum Miles Card Program");
        cardProgram.setCardRewardType(RewardType.MILES);
        cardProgramRepository.save(cardProgram);

        // create a new card
        Card card = new Card("123456789H", cardProgram, "john8.doe@example.com", "1234567890", aesEncryptionUtil);
        cardRepository.save(card);

        // create a new transaction request
        TransactionRequest transactionRequest = new TransactionRequest(
                aesEncryptionUtil.decrypt(card.getCardNumber()),
                "01/01/2022",
                "scis_freedom",
                1500.0,
                "SGD",
                "Test Merchant",
                9399 //  Exclusion includes: 6051,9399,6540
        );

        // Check if MCC is part of the exclusion
        assertEquals(false, merchantCategoryCodeExclusionService.checkTransactionEligibility(
                transactionRequest.merchantCategoryCode()));

        // call the addTransaction method
        Transaction createdTransaction = transactionService.addTransaction(transactionRequest);

        // assert that the transaction was created successfully
        assertNotNull(createdTransaction);
        assertEquals(transactionRequest.transactionAmount(), createdTransaction.getTransactionAmount());
        assertEquals(transactionRequest.transactionCurrency(), createdTransaction.getTransactionCurrency());
        assertEquals(transactionRequest.merchantName(), createdTransaction.getMerchantName());
        assertEquals(transactionRequest.merchantCategoryCode(), createdTransaction.getMerchantCategoryCode());

        // assert that the reward earned is $0.0
        assertEquals(0.0, createdTransaction.getTransactionRewardEarned());

    }

    @Test
    @Order(11)
    public void testExclusionForMCC6540() throws ParseException {
        // create a new card program
        CardProgram cardProgram = new CardProgram();
        cardProgram.setCardProgram("scis_freedom");
        cardProgram.setCardProgramDescription("Platinum Miles Card Program");
        cardProgram.setCardRewardType(RewardType.MILES);
        cardProgramRepository.save(cardProgram);

        // create a new card
        Card card = new Card("123456789I", cardProgram, "john9.doe@example.com", "1234567890", aesEncryptionUtil);
        cardRepository.save(card);

        // create a new transaction request
        TransactionRequest transactionRequest = new TransactionRequest(
                aesEncryptionUtil.decrypt(card.getCardNumber()),
                "01/01/2022",
                "scis_freedom",
                1500.0,
                "SGD",
                "Test Merchant",
                6540 //  Exclusion includes: 6051,9399,6540
        );

        // Check if MCC is part of the exclusion
        assertEquals(false, merchantCategoryCodeExclusionService.checkTransactionEligibility(
                transactionRequest.merchantCategoryCode()));

        // call the addTransaction method
        Transaction createdTransaction = transactionService.addTransaction(transactionRequest);

        // assert that the transaction was created successfully
        assertNotNull(createdTransaction);
        assertEquals(transactionRequest.transactionAmount(), createdTransaction.getTransactionAmount());
        assertEquals(transactionRequest.transactionCurrency(), createdTransaction.getTransactionCurrency());
        assertEquals(transactionRequest.merchantName(), createdTransaction.getMerchantName());
        assertEquals(transactionRequest.merchantCategoryCode(), createdTransaction.getMerchantCategoryCode());

        // assert that the reward earned is $0.0
        assertEquals(0.0, createdTransaction.getTransactionRewardEarned());

    }


}
