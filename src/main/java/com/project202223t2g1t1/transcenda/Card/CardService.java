package com.project202223t2g1t1.transcenda.Card;



import com.project202223t2g1t1.transcenda.Campaign.CampaignService;
import com.project202223t2g1t1.transcenda.MerchantExclusion.MerchantCategoryCodeExclusionService;
import com.project202223t2g1t1.transcenda.Security.AESEncryptionUtil;
import com.project202223t2g1t1.transcenda.Transaction.TransactionRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final CardProgramRepository cardProgramRepository;
    private final CardProgramRewardRateRepository cardProgramRewardRateRepository;
    private final MerchantCategoryCodeExclusionService merchantCategoryCodeExclusionService;
    private final CampaignService campaignService;
    private final AESEncryptionUtil aesEncryptionUtil;

    public CardService(CardRepository cardRepository, CardProgramRepository cardProgramRepository,
                       CardProgramRewardRateRepository cardProgramRewardRateRepository, MerchantCategoryCodeExclusionService merchantCategoryCodeExclusionService,
                       CampaignService campaignService, AESEncryptionUtil aesEncryptionUtil) {
        this.cardRepository = cardRepository;
        this.cardProgramRepository = cardProgramRepository;
        this.cardProgramRewardRateRepository = cardProgramRewardRateRepository;
        this.merchantCategoryCodeExclusionService = merchantCategoryCodeExclusionService;
        this.campaignService = campaignService;
        this.aesEncryptionUtil = aesEncryptionUtil;
    }

    public String addCard(CardRegistrationRequest card) {
        //verify card program exist
        CardProgram cardProgram;
        try {
            // todo: check if card program exist
            // if null throw exception
            cardProgram = cardProgramRepository.findCardProgramByCardProgram(card.cardProgram());
            if (cardProgram == null) {
                throw new IllegalStateException("Card program does not exist");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Card program does not exist");
        }


        // create new card, card number base on existing Excel file.
        // Assumption: we will not need to generate our own card number
        Card new_card = new Card(card.cardNumber(), cardProgram, card.userEmail(),card.userContactNumber(), aesEncryptionUtil);

        return cardRepository.save(new_card).getCardNumber();
    }

    public Card findCardByCardNumber(String cardNumber) {
        String encryptedNumber = aesEncryptionUtil.encrypt(cardNumber);
        return cardRepository.findCardByCardNumber(encryptedNumber);
    }

    // RETRIEVE ALL CARDS OF A USER
    public List<Card> retrieveAllCards(String userEmail) {
        return cardRepository.findAllCardsWithProgramByUserEmail(userEmail);
    }

    // RETRIEVE ALL REWARD BALANCES OF A USER
    public List<String> retrieveAllRewardBalances(String userEmail) {
        List<String> rewardBalances = new ArrayList<>();

        rewardBalances.add("pointsReward=" + retrievePointsBalance(userEmail));
        rewardBalances.add("milesReward=" + retrieveMilesBalance(userEmail));
        rewardBalances.add("cashbackReward=" + retrieveCashbackBalance(userEmail));

        return rewardBalances;
    }

    // RETRIEVE POINTS BALANCE OF A USER
    public double retrievePointsBalance(String userEmail) {
        List<Card> cards = cardRepository.findCardsByUserEmail(userEmail);

        double totalPoints = 0.0;

        for (Card card : cards) {
            if(card.getCardProgram().getCardProgram().equals("scis_shopping")){
                totalPoints += card.getRewardEarned();
                break;
            }
        }

        return totalPoints;
    }
    // RETRIEVE MILES BALANCE OF A USER
    public double retrieveMilesBalance(String userEmail) {
        List<Card> cards = cardRepository.findCardsByUserEmail(userEmail);

        double totalMiles = 0.0;

        for (Card card : cards) {
            if (card == null) {
                continue;
            }
            if (card.getCardProgram().getCardProgram().equals("scis_platinummiles") || card.getCardProgram().getCardProgram().equals("scis_premiummiles")) {
                totalMiles += card.getRewardEarned(); // there are 2 types of miles card, so we need to add them together
            }
        }

        return totalMiles;
    }
    // RETRIEVE CASHBACK BALANCE OF A USER
    public double retrieveCashbackBalance(String userEmail) {
        List<Card> cards = cardRepository.findCardsByUserEmail(userEmail);

        double totalCashback = 0.0;

        for (Card card : cards) {
            if(card.getCardProgram().getCardProgram().equals("scis_freedom")){
                totalCashback += card.getRewardEarned();
                break;
            }
        }

        return totalCashback;
    }


    // PROCESSING TRANSACTIONS
    // SCIS Shopping Card
    public Map<Integer,String> processShoppingCardTransaction(Card card, TransactionRequest transaction) {
        double baseEarnRate = 0.0;
        double bonusEarnRate = 0.0;
        double campaignRewardRate = 0.0;
        double totalRewardEarned = 0.0;
        boolean isForeignSpending = !transaction.transactionCurrency().equals("SGD");
        String remarks = "";
        Map<Integer,String> remarksMap =new HashMap<Integer,String>();

        // Check for eligibility
        if (!merchantCategoryCodeExclusionService.checkTransactionEligibility(transaction.merchantCategoryCode())) {
            remarksMap.put(1,"Transaction not eligible for reward points");
            remarksMap.put(2,"0.0");
            remarksMap.put(3,"No campaign reward earned");
            remarksMap.put(4,"0.0");
            return remarksMap;
        }

        List <Integer> onlineShopping = new ArrayList<>();
        onlineShopping.add(5311);
        onlineShopping.add(5399);
        onlineShopping.add(5411);
        onlineShopping.add(5691);
        onlineShopping.add(5964);
        onlineShopping.add(5999);

        // Retrieve card program
        CardProgram cardProgram = card.getCardProgram();

        // retrieve base earn rate and bonus earn rate
        if(onlineShopping.contains(transaction.merchantCategoryCode())){
            //Online shopping
            baseEarnRate = cardProgramRewardRateRepository.findCardProgramRewardRateByCardProgramAndEarnRateMerchantCategory(cardProgram, MerchantCategory.SHOPPING).get(0).getEarnRate();
            bonusEarnRate = cardProgramRewardRateRepository.findCardProgramRewardRateByCardProgramAndEarnRateMerchantCategory(cardProgram, MerchantCategory.ONLINE_SHOPPING).get(0).getEarnRate();
            remarks = "Earned base rate of " + baseEarnRate + " point(s) per SGD spend and bonus rate of " + bonusEarnRate + " point(s) per SGD spend for online shopping transaction.";
        } else if (transaction.merchantCategoryCode() >= 5000 && transaction.merchantCategoryCode() <= 5999){
            // Shopping
            baseEarnRate = cardProgramRewardRateRepository.findCardProgramRewardRateByCardProgramAndEarnRateMerchantCategory(cardProgram, MerchantCategory.SHOPPING).get(0).getEarnRate();
            remarks = "Earned base rate of " + baseEarnRate + " point(s) per SGD spend for shopping transaction.";
        } else {
            // Other
            baseEarnRate = cardProgramRewardRateRepository.findCardProgramRewardRateByCardProgramAndEarnRateMerchantCategory(cardProgram, MerchantCategory.OTHER).get(0).getEarnRate();
            remarks = "Earned base rate of " + baseEarnRate + " point(s) per SGD spend for the transaction.";
        }

        // calculate total reward earned
        if(isForeignSpending){
            totalRewardEarned = (transaction.transactionAmount() * 1.35) * (baseEarnRate + bonusEarnRate);
        } else {
            totalRewardEarned = transaction.transactionAmount() * (baseEarnRate + bonusEarnRate);
        }

        // check if the transaction is eligible for any campaigns
        //pass to campaign service to check if the transaction is eligible for any campaigns relating to card program
        //if eligible, capture the bonus and add it to the total Reward earned
        campaignRewardRate = campaignService.processCampaignTransaction(transaction);

        if (campaignRewardRate > 0) {
            Double campaignReward = transaction.transactionAmount() * campaignRewardRate;
            totalRewardEarned += campaignReward;
            remarks += " Additional " + campaignRewardRate + " point(s) per SGD spend for campaign bonus.";
            remarksMap.put(3,". Congrats you have earn a campaign bonus of "+ campaignReward + ". Campaign bonus was " + campaignRewardRate + " point(s) per SGD spend.");
            remarksMap.put(4,String.valueOf(campaignReward));
        }

        //update card balance
        card.setRewardEarned(card.getRewardEarned() + totalRewardEarned);
        cardRepository.save(card);

        remarksMap.put(1,remarks);
        remarksMap.put(2, String.valueOf(totalRewardEarned));

        return remarksMap;
    }

    // SCIS PremiumMiles Card
    public Map<Integer,String> processMilesCardTransaction(Card card, TransactionRequest transaction) {
        double baseEarnRate = 0.0;
        double bonusEarnRate = 0.0;
        double campaignRewardRate = 0.0;
        double totalRewardEarned = 0.0;
        boolean isForeignSpending = !transaction.transactionCurrency().equals("SGD");
        String remarks = "";
        Map<Integer,String> remarksMap =new HashMap<Integer,String>();

        if (!merchantCategoryCodeExclusionService.checkTransactionEligibility(transaction.merchantCategoryCode())) {
            remarksMap.put(1,"Transaction not eligible for reward points");
            remarksMap.put(2,"0.0");
            remarksMap.put(3,"No campaign reward earned");
            remarksMap.put(4,"0.0");
            return remarksMap;
        }

        CardProgram cardProgram = card.getCardProgram();

        //find base earn rate
        if (isForeignSpending){
            baseEarnRate = cardProgramRewardRateRepository.findCardProgramRewardRateByCardProgramAndEarnRateMerchantCategory(cardProgram, MerchantCategory.FOREIGN_OTHER).get(0).getEarnRate();
        } else {
            baseEarnRate = cardProgramRewardRateRepository.findCardProgramRewardRateByCardProgramAndEarnRateMerchantCategory(cardProgram, MerchantCategory.OTHER).get(0).getEarnRate();
        }

        //find bonus earn rate
        if (isForeignSpending && transaction.merchantCategoryCode() >= 3500 && transaction.merchantCategoryCode() <= 3999){
            //check if scis_premiummiles or scis_platinummiles
            if (cardProgram.getCardProgram().equals("scis_premiummiles")){
                bonusEarnRate = cardProgramRewardRateRepository.findCardProgramRewardRateByCardProgramAndEarnRateMerchantCategory(cardProgram, MerchantCategory.HOTEL).get(0).getEarnRate();
            } else {
                bonusEarnRate = cardProgramRewardRateRepository.findCardProgramRewardRateByCardProgramAndEarnRateMerchantCategory(cardProgram, MerchantCategory.FOREIGN_HOTEL).get(0).getEarnRate();
            }
        } else if (transaction.merchantCategoryCode() >= 3500 && transaction.merchantCategoryCode() <= 3999){
            bonusEarnRate = cardProgramRewardRateRepository.findCardProgramRewardRateByCardProgramAndEarnRateMerchantCategory(cardProgram, MerchantCategory.HOTEL).get(0).getEarnRate();
        }

        if(isForeignSpending){
            totalRewardEarned = (transaction.transactionAmount() * 1.35) * (baseEarnRate + bonusEarnRate);
        } else {
            totalRewardEarned = transaction.transactionAmount() * (baseEarnRate + bonusEarnRate);
        }

        // check if the transaction is eligible for any campaigns relating to card program
        campaignRewardRate = campaignService.processCampaignTransaction(transaction);

        if (campaignRewardRate > 0) {
            Double campaignReward = transaction.transactionAmount() * campaignRewardRate;
            totalRewardEarned += campaignReward;
            remarks += " Additional " + campaignRewardRate + "point(s) per SGD spend for campaign bonus.";
            remarksMap.put(3,". Congrats you have earn a campaign bonus of "+ campaignReward + ". Campaign bonus was " + campaignRewardRate + " mile(s) per SGD spend.");
            remarksMap.put(4,String.valueOf(campaignReward));
        }

        // Update card balance
        card.setRewardEarned(card.getRewardEarned() + totalRewardEarned);
        cardRepository.save(card);

        if (bonusEarnRate != 0.0){
            remarks = "Earned base rate of " + baseEarnRate + " mile(s) per SGD spend and bonus rate of " + bonusEarnRate + " mile(s) per SGD spend for the transaction.";
        } else {
            remarks = "Earned base rate of " + baseEarnRate + " mile(s) per SGD spend for the transaction.";
        }

        remarksMap.put(1,remarks);
        remarksMap.put(2, String.valueOf(totalRewardEarned));
        return remarksMap;
    }

    // SCIS Freedom Card
    public Map<Integer,String> processCashbackCardTransaction(Card card, TransactionRequest transaction) {
        double baseEarnRate = 0.0;
        double totalRewardEarned = 0.0;
        double campaignRewardRate = 0.0;
        double amountSpent = transaction.transactionAmount();
        boolean isForeignSpending = !transaction.transactionCurrency().equals("SGD");
        String remarks = "";
        Map<Integer,String> remarksMap =new HashMap<Integer,String>();

        if (!merchantCategoryCodeExclusionService.checkTransactionEligibility(transaction.merchantCategoryCode())) {
            remarksMap.put(1,"Transaction not eligible for reward points");
            remarksMap.put(2,"0.0");
            remarksMap.put(3,"No campaign reward earned");
            remarksMap.put(4,"0.0");
            return remarksMap;
        }

        if (isForeignSpending){
            amountSpent = amountSpent * 1.35;
        }

        //find base earn rate 0.5% base , 1.0% spend > 500 SGD and 3.0% spend > 2000 SGD
        if (transaction.transactionAmount() >= 2000){
            baseEarnRate = 0.005;
        } else if (transaction.transactionAmount() >= 500){
            baseEarnRate = 0.01;
        } else {
            baseEarnRate = 0.03;
        }

        // Calculate total reward earned
        totalRewardEarned = amountSpent * baseEarnRate;

        campaignRewardRate = campaignService.processCampaignTransaction(transaction);

        // check if the transaction is eligible for any campaigns relating to card program
        if (campaignRewardRate > 0) {
            Double campaignReward = transaction.transactionAmount() * campaignRewardRate;
            totalRewardEarned += campaignReward;
            remarks += " Additional " + campaignRewardRate + "point(s) per SGD spend for campaign bonus.";
            remarksMap.put(3,". Congrats you have earned a campaign bonus of "+ campaignReward + "%. Campaign bonus was $" + campaignRewardRate + "cashback for the total amount spent");
            remarksMap.put(4,String.valueOf(campaignReward));
        }

        // Update card balance
        card.setRewardEarned(card.getRewardEarned() + totalRewardEarned);
        cardRepository.save(card);

        remarks = "Earned cashback of " + baseEarnRate + "% for the total amount of the transaction.";

        remarksMap.put(1,remarks);
        remarksMap.put(2, String.valueOf(totalRewardEarned));

        return remarksMap;
    }


    public CardProgram addCardType(CardProgram cardProgram) {
        return cardProgramRepository.save(cardProgram);
    }

    public List<CardProgram> getAllCardProgram() {
        return cardProgramRepository.findAll();
    }


    public CardProgram getCardProgram(String cardProgram) {
        return cardProgramRepository.findCardProgramByCardProgram(cardProgram);
    }

    public CardProgram findCardProgram(String cardProgram) {
        return cardProgramRepository.findCardProgramByCardProgram(cardProgram);
    }
}
