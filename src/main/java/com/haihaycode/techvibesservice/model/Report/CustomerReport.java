package com.haihaycode.techvibesservice.model.Report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerReport {
    private Long customerId;
    private String customerName;
    private Long orderCount;

}