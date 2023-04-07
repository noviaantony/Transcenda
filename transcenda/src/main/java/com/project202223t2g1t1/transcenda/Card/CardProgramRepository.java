package com.project202223t2g1t1.transcenda.Card;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardProgramRepository extends JpaRepository<CardProgram, String> {
    CardProgram findCardProgramByCardProgram(String cardProgramName);
}
