package com.project202223t2g1t1.transcenda.MerchantExclusion;



import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MerchantCategoryCodeExclusion {
    @Id
    private Integer merchantCategoryCode;
    private String mccDescription;
    private String mccMerchantName;
}
