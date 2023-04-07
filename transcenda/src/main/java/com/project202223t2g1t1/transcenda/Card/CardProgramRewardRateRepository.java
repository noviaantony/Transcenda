package com.project202223t2g1t1.transcenda.Card;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CardProgramRewardRateRepository extends JpaRepository<CardProgramRewardRate, Long> {
    List<CardProgramRewardRate> findCardProgramRewardRateByCardProgramAndEarnRateMerchantCategory(CardProgram cardProgram, MerchantCategory earnRateMerchantCategory);
//    List<Double> findEarnRateByCardProgramAndEarnRateMerchantCategory(CardProgram cardProgram, MerchantCategory earnRateMerchantCategory);
}
