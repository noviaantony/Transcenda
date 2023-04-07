package com.project202223t2g1t1.transcenda.Card;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Card findCardByCardNumber(String cardNumber);

    List<Card> findCardsByUserEmail(String userEmail);

    List<Card> findCardByCardNumberAndCardProgram(String cardNumber, CardProgram cardProgram);

    List<Card> findAllByUserEmail(String userEmail);

//    @Query("SELECT c FROM Card c JOIN FETCH c.cardProgram WHERE c.userEmail = :userEmail")
//    List<Card> findCardsByUserEmailWithProgram(@Param("userEmail") String userEmail);

    @Query("SELECT c FROM Card c LEFT JOIN FETCH c.cardProgram WHERE c.userEmail = :userEmail")
    List<Card> findAllCardsWithProgramByUserEmail(@Param("userEmail") String userEmail);
}
