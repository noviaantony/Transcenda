package com.project202223t2g1t1.transcenda.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findTop20ByUserIdOrderByTransactionDateDesc(String userId);

    List<Transaction> findAllByUserIdOrderByTransactionDateDesc(String userId);
}
