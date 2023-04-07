package com.project202223t2g1t1.transcenda.MerchantExclusion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantCategoryCodeExclusionRepository extends JpaRepository<MerchantCategoryCodeExclusion, Integer> {

}
