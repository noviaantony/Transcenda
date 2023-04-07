package com.project202223t2g1t1.transcenda.Card;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transcenda/card")
public class CardController {
    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    // Card APIs

    @GetMapping(path = "/rewardbalances")
    public ResponseEntity<List<String>> getRewardBalances(@RequestParam("userEmail") String userEmail) {
        return new ResponseEntity<>(cardService.retrieveAllRewardBalances(userEmail), HttpStatus.OK);
    }

    @GetMapping(params="userEmail")
    public ResponseEntity<List<Card>> getCards(@RequestParam("userEmail") String userEmail) {
        return new ResponseEntity<>(cardService.retrieveAllCards(userEmail), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> addCard(@RequestBody CardRegistrationRequest cardRegistrationRequest) {
        try {
            return new ResponseEntity<>(cardService.addCard(cardRegistrationRequest), HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            // depends on the error
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // Card Program APIs
    @GetMapping("/card-type")
    public ResponseEntity <List<CardProgram>> getAllCardProgram() {
        return new ResponseEntity<>(cardService.getAllCardProgram(), HttpStatus.OK);
    }

    @GetMapping("/getCard-type")
    public ResponseEntity <CardProgram> getCardProgram(@RequestParam("cardProgram") String cardProgram) {
        return new ResponseEntity<>(cardService.getCardProgram(cardProgram), HttpStatus.OK);
    }

    @PostMapping("/card-type")
    public void addCardType(CardProgram cardProgram) {
        cardService.addCardType(cardProgram);
    }

}
