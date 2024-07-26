package com.haihaycode.techvibesservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {
    private String name;
    private Long price;
    private String description;
    private String descriptionSort;
    private Long categoryId;
}
