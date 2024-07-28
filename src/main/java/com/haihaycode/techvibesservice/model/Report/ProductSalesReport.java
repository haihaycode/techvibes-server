package com.haihaycode.techvibesservice.model.Report;

import com.haihaycode.techvibesservice.entity.ProductEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSalesReport {
    private ProductEntity productEntity;
    private Long totalQuantity;

    public ProductSalesReport() {}

    public ProductSalesReport(ProductEntity productEntity, Long totalQuantity) {
        this.productEntity = productEntity;
        this.totalQuantity = totalQuantity;
    }

}
