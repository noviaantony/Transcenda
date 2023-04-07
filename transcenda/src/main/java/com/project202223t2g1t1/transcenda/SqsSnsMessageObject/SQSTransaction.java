package com.project202223t2g1t1.transcenda.SqsSnsMessageObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class SQSTransaction {
    private String id;
    private String card_id;
    private String merchant;
    private Integer mcc;
    private String currency;
    private Double amount;
    private Double sgd_amount;
    private String transaction_id;
    private String transaction_date;
    private String card_pan;
    private String card_type;

    public String toString(){
        return "SQSTransaction [id=" + id + ", card_id=" + card_id + ", merchant=" + merchant + ", mcc=" + mcc + ", " +
                "currency=" + currency + ", amount=" + amount + ", sgd_amount=" + sgd_amount + ", transaction_id=" + transaction_id + ", " +
                "transaction_date=" + transaction_date + ", card_pan=" + card_pan + ", card_type=" + card_type + "]";
    }
}
