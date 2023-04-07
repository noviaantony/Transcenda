package com.project202223t2g1t1.transcenda.SqsSnsMessageObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project202223t2g1t1.transcenda.Transaction.TransactionRequest;
import com.project202223t2g1t1.transcenda.Transaction.TransactionService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class SQSListener {
    private static final Logger logger = LogManager.getLogger(SqsListener.class);

    private final TransactionService transactionService;
    private final InMemoryDeduplicationService inMemoryDeduplicationService;

    @Autowired
    private	SQSListener(TransactionService transactionService, InMemoryDeduplicationService inMemoryDeduplicationService) {
        this.transactionService = transactionService;
        this.inMemoryDeduplicationService = inMemoryDeduplicationService;
    }

    // This is a test to see if the SQS listener works
    // after receiving message we will send it to the transaction service for processing
    @SqsListener("Transaction-Queue")
    public void listen(String message) {
        String messageWithoutBom = message.replace("\\uFEFF", "");
        SQSTransaction sqsTransaction = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println("Received message: " + messageWithoutBom);
            sqsTransaction = mapper.readValue(messageWithoutBom, SQSTransaction.class);
            System.out.println("Received transaction: " + sqsTransaction);
            if (sqsTransaction == null) {
                logger.warn("SQSListener: SQSTransaction is null");
                throw new Exception("SQSListener: SQSTransaction is null");
            }
            String messageId = sqsTransaction.getId();
            if (inMemoryDeduplicationService.isMessageProcessed(messageId)) {
                // Message has already been processed, discard it
                System.out.println("Message has already been processed, discarding it");
                logger.warn("Message has already been processed, discarding it");
                return;
            }

            System.out.println("Adding transaction!");
            if (sqsTransaction.getMcc() == 0) {
                System.out.println("MCC is 0, discarding it");
                logger.warn("MCC is 0, discarding it");
                return ;
            }
            TransactionRequest transactionRequest = new TransactionRequest(
                    sqsTransaction.getCard_id(), sqsTransaction.getTransaction_date(),
                    sqsTransaction.getCard_type(), sqsTransaction.getAmount(),
                    sqsTransaction.getCurrency(), sqsTransaction.getMerchant(), sqsTransaction.getMcc());

            System.out.println("Transaction request: " + transactionRequest);
            transactionService.addTransaction(transactionRequest);
            System.out.println("Transaction added successfully!");
        }catch(Exception e) {
            logger.error("Error in SQSListener: " + e.getMessage());
            System.out.println("Error in SQSListener: " + e);
        }


    }
}
