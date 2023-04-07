package com.project202223t2g1t1.transcenda.Transaction;

import com.project202223t2g1t1.transcenda.Exception.MerchantCategoryCodeNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping(path ="api/v1/transcenda/transaction")
public class  TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping(params = "userEmail")
    public ResponseEntity<List<Transaction>> getTransaction(@RequestParam("userEmail" ) String userEmail)  {
        return new ResponseEntity<>(transactionService.retrieveAllTransactions(userEmail), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Transaction> addTransaction(@RequestBody TransactionRequest transactionRequest) throws ParseException {
        return new ResponseEntity<>(transactionService.addTransaction(transactionRequest), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getRecent50Transactions() {
        return new ResponseEntity<>(transactionService.getRecent50Transactions(), HttpStatus.OK);
    }
    @GetMapping(path ="/alltransactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return new ResponseEntity<>(transactionService.getAllTransactions(), HttpStatus.OK);
    }

}
