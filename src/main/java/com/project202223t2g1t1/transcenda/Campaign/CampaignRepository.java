package com.project202223t2g1t1.transcenda.Campaign;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    Campaign findByMerchantName(String merchantName);
}
