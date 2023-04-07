package com.project202223t2g1t1.transcenda.Transaction;

import com.project202223t2g1t1.transcenda.Card.Card;
import com.project202223t2g1t1.transcenda.Card.CardService;
//import com.project202223t2g1t1.transcenda.SqsSnsMessageObject.SnsService;
import com.project202223t2g1t1.transcenda.SqsSnsMessageObject.SnsService;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Version;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

@Service
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CardService cardService;
    private final SnsService snsService;

    public TransactionService(TransactionRepository transactionRepository, CardService cardService, SnsService snsService) {
        this.transactionRepository = transactionRepository;
        this.cardService = cardService;
        this.snsService = snsService;
    }

    // RETRIEVE ALL TRANSACTIONS FOR A USER BASED ON USER ID
    public List<Transaction> retrieveAllTransactions(String userId) {
        //retrieve the recent 20 transactions for the user
        return transactionRepository.findAllByUserIdOrderByTransactionDateDesc(userId);
    }

    // ADD A NEW TRANSACTION
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Transaction addTransaction(TransactionRequest transactionRequest) throws ParseException {
        // retrieve card used to make transaction

        Card card = cardService.findCardByCardNumber(transactionRequest.cardNumber());

        if (card == null) {
            throw new NullPointerException("Card does not exist in the system");
        }

        Double rewardBalanceAfterTransaction = 0.0;
        //create transaction
        Map<Integer,String> remarks=new HashMap<Integer,String>();


        try {
            // pass to card service to process transaction
            if (transactionRequest.cardProgramType().equals("scis_shopping")) {
                remarks = cardService.processShoppingCardTransaction(card, transactionRequest);
                rewardBalanceAfterTransaction = cardService.retrievePointsBalance(card.getUserEmail());
            } else if (transactionRequest.cardProgramType().equals("scis_platinummiles") || transactionRequest.cardProgramType().equals("scis_premiummiles")) {
                remarks = cardService.processMilesCardTransaction(card, transactionRequest);
                rewardBalanceAfterTransaction = cardService.retrieveMilesBalance(card.getUserEmail());
            } else if (transactionRequest.cardProgramType().equals("scis_freedom")) {
                remarks = cardService.processCashbackCardTransaction(card, transactionRequest);
                rewardBalanceAfterTransaction = cardService.retrieveCashbackBalance(card.getUserEmail());
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid transaction arguments");
        }

        StringTokenizer st = new StringTokenizer(transactionRequest.transactionDate(), "/");
        int day = Integer.parseInt((String) st.nextElement());
        int month = Integer.parseInt((String) st.nextElement());
        int year = Integer.parseInt((String) st.nextElement());

        LocalDate date = LocalDate.of(year, month, day);
        Transaction transaction = new Transaction(
                date,
                card.getUserEmail(),
                transactionRequest.transactionAmount(),
                Double.parseDouble(remarks.get(2)),
                rewardBalanceAfterTransaction,
                transactionRequest.transactionCurrency(),
                transactionRequest.cardProgramType(),
                transactionRequest.merchantName(),
                remarks.get(1),
                transactionRequest.merchantCategoryCode(),
                card
        );

        //send sns message
        if (remarks.get(4) != null && Double.parseDouble(remarks.get(4)) > 0.0) {
//            snsService.sendRewardEmail(card.getUserEmail(),remarks.get(3));
            snsService.sendSms(card.getUserContactNumber(),"Campaign Merchant: " + transactionRequest.merchantName() + remarks.get(3));
        }

        //save transaction
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getRecent50Transactions() {
        Page<Transaction> page = transactionRepository.findAll(PageRequest.of(0,50, Sort.by(Sort.Direction.DESC,"transactionId")));
        List<Transaction> list = page.getContent();
        return list;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll(Sort.by(Sort.Direction.ASC,"transactionId"));
    }
}
