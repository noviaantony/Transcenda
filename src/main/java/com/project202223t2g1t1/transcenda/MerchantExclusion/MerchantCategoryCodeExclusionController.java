package com.project202223t2g1t1.transcenda.MerchantExclusion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path ="api/v1/transcenda/merchant-category-code-exclusion")
public class MerchantCategoryCodeExclusionController {
    private final MerchantCategoryCodeExclusionService merchantCategoryCodeExclusionService;

    public MerchantCategoryCodeExclusionController(MerchantCategoryCodeExclusionService merchantCategoryCodeExclusionService) {
        this.merchantCategoryCodeExclusionService = merchantCategoryCodeExclusionService;
    }

    //Rest API
    //Get all merchant category code exclusion
    @GetMapping
    public ResponseEntity<List<MerchantCategoryCodeExclusion>> retrieveAllMerchantCategoryCodeExclusion(){
        return new ResponseEntity<>(merchantCategoryCodeExclusionService.retrieveAllMerchantCategoryCodeExclusion(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> addMerchantCategoryCodeExclusion(@RequestBody MerchantCategoryCodeExclusionRequest merchantCategoryCodeExclusionRequest){
        return new ResponseEntity<>(merchantCategoryCodeExclusionService.addMerchantCategoryCodeExclusion(merchantCategoryCodeExclusionRequest), HttpStatus.CREATED);
    }

    @DeleteMapping(params = "merchantCategoryCode")
    public ResponseEntity<String> deleteMerchantCategoryCodeExclusion(@RequestParam Integer merchantCategoryCode){
        return new ResponseEntity<>(merchantCategoryCodeExclusionService.deleteMerchantCategoryCodeExclusion(merchantCategoryCode), HttpStatus.OK);
    }
}
