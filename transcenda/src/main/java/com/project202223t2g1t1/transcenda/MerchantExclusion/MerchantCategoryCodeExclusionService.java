package com.project202223t2g1t1.transcenda.MerchantExclusion;

import com.project202223t2g1t1.transcenda.Exception.MerchantCategoryCodeNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantCategoryCodeExclusionService {
    private final MerchantCategoryCodeExclusionRepository merchantCategoryCodeExclusionRepository;

    public MerchantCategoryCodeExclusionService(MerchantCategoryCodeExclusionRepository merchantCategoryCodeExclusionRepository) {
        this.merchantCategoryCodeExclusionRepository = merchantCategoryCodeExclusionRepository;
    }

    public MerchantCategoryCodeExclusion getMerchantCategoryCode(Integer merchantCategoryCode) throws MerchantCategoryCodeNotFoundException {
        return merchantCategoryCodeExclusionRepository.findById(merchantCategoryCode).orElseThrow(
                () -> new MerchantCategoryCodeNotFoundException("Merchant Category Code not found"));
    }

    public boolean checkTransactionEligibility(Integer merchantCategoryCode){
        try{
            getMerchantCategoryCode(merchantCategoryCode);
            return false;
        } catch (MerchantCategoryCodeNotFoundException e){
            return true;
        }
    }

    public List<MerchantCategoryCodeExclusion> retrieveAllMerchantCategoryCodeExclusion() {
        return merchantCategoryCodeExclusionRepository.findAll();
    }

    public String addMerchantCategoryCodeExclusion(MerchantCategoryCodeExclusionRequest merchantCategoryCodeExclusionRequest) {
        MerchantCategoryCodeExclusion merchantCategoryCodeExclusion = new MerchantCategoryCodeExclusion(merchantCategoryCodeExclusionRequest.merchantCategoryCode(), merchantCategoryCodeExclusionRequest.mccDescription(), merchantCategoryCodeExclusionRequest.mccMerchantName());
        merchantCategoryCodeExclusionRepository.save(merchantCategoryCodeExclusion);
        return "Merchant Category Code: "+ merchantCategoryCodeExclusionRequest.merchantCategoryCode() +" exclusion added";
    }

    public String deleteMerchantCategoryCodeExclusion(Integer merchantCategoryCode) {
        //check if merchant category code exists
        try{
            getMerchantCategoryCode(merchantCategoryCode);
        } catch (MerchantCategoryCodeNotFoundException e){
            return "Merchant Category Code: "+ merchantCategoryCode +" does not exist";
        }

        merchantCategoryCodeExclusionRepository.deleteById(merchantCategoryCode);
        return "Merchant Category Code: "+ merchantCategoryCode +" exclusion deleted";
    }
}
