package com.example.shop.service.Impl;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShiroRealm extends AuthorizingRealm {
    private final VerificationCodeCheckService verificationCodeCheckService;

    @Autowired
    public ShiroRealm(VerificationCodeCheckService verificationCodeCheckService) {
        this.verificationCodeCheckService = verificationCodeCheckService;
        this.setCredentialsMatcher((token, info) -> new String((char[]) token.getCredentials()).equals(info.getCredentials()));
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String tel = (String) authenticationToken.getPrincipal();
        String correctTelCode = verificationCodeCheckService.getCorrectTelCode(tel);
        return new SimpleAuthenticationInfo(tel, correctTelCode, getName());
    }
}
