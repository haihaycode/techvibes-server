package com.haihaycode.techvibesservice.model.Report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSalesCount {
    private Long productId;
    private String productName;
    private Long totalQuantity;

    public ProductSalesCount(Long productId, String productName, Long totalQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.totalQuantity = totalQuantity;
    }

    // Getters and Setters
}
