package com.haihaycode.techvibesservice.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;


/*
* cụ thể lớp này chứa thông tin người dùng khi đã xác thực thành công
*
* */
public class UserPrincipalAuthenticationToken extends AbstractAuthenticationToken {
    private final UserPrincipal userPrincipal;
    public UserPrincipalAuthenticationToken(UserPrincipal userPrincipal) {
        super(userPrincipal.getAuthorities());
        this.userPrincipal= userPrincipal;
        setAuthenticated(true);
    }


    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public UserPrincipal getPrincipal() {
        return userPrincipal;
    }


}
