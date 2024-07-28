package com.haihaycode.techvibesservice.model.Report;

import com.haihaycode.techvibesservice.entity.ProductEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RevenueReport {
    private ProductEntity productEntity;
    private Long totalRevenue;

    public RevenueReport() {
    }

    public RevenueReport(ProductEntity productEntity, Long totalRevenue) {
        this.productEntity = productEntity;
        this.totalRevenue = totalRevenue;
    }
}
