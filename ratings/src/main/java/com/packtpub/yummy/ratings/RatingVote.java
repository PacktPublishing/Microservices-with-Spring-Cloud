package com.packtpub.yummy.ratings;

import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Data
public class RatingVote {
    @DecimalMin("0.5") @DecimalMax("5")
    BigDecimal rating;
}
