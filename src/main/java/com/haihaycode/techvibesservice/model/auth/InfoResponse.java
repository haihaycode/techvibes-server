package com.haihaycode.techvibesservice.model.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Builder
public class InfoResponse {
    private Long userId;
    private String email;
    private String address;
    private Integer phone;
    private String fullName;
    private String photo;
    private Set<String> roles;
    private Date createDate;
    private Date updateDate;
}
