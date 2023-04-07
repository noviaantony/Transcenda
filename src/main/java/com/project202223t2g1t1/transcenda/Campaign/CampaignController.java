package com.project202223t2g1t1.transcenda.Campaign;

import com.project202223t2g1t1.transcenda.Card.CardProgram;
import com.project202223t2g1t1.transcenda.Card.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("api/v1/transcenda/campaign")
public class CampaignController {
    private final CampaignService campaignService;
    private final CardService cardService;

    public CampaignController(CampaignService campaignService, CardService cardService) {
        this.campaignService = campaignService;
        this.cardService = cardService;
    }

    // Campaign APIs
    @GetMapping
    public ResponseEntity<List<Campaign>> getAllCampaigns() {
        return ResponseEntity.ok(campaignService.getAllCampaigns());
    }

    @GetMapping(params="merchantName")
    public ResponseEntity<Campaign> getCampaign(@RequestParam("merchantName") String merchantName) {
        return ResponseEntity.ok(campaignService.getCampaign(merchantName));
    }

    @PostMapping
    public ResponseEntity<Campaign> createNewCampaign(@RequestBody CampaignRequest campaignRequest) {
        //Verify Card Program
        CardProgram cardProgram = cardService.findCardProgram(campaignRequest.cardProgramName());
        if (cardProgram == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(campaignService.createNewCampaign(campaignRequest, cardProgram), HttpStatus.CREATED);
    }

    @DeleteMapping(params="campaignId")
    public ResponseEntity<String> deleteCampaign(@RequestParam("campaignId") Long campaignId) {
        return new ResponseEntity<>(campaignService.deleteCampaign(campaignId), HttpStatus.OK);
    }
}
