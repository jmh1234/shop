package com.example.shop.config;

import com.example.shop.interceptor.UserLoginInterceptor;
import com.example.shop.service.authority.ShiroRealmService;
import com.example.shop.service.authority.UserService;
import com.example.shop.service.authority.VerificationCodeCheckService;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShiroConfig implements WebMvcConfigurer {

    private final UserService userService;

    @Autowired
    public ShiroConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, String> pattern = new HashMap<>();
        pattern.put("/api/code", "anon");
        pattern.put("/api/login", "anon");
//        pattern.put("/api/status", "anon");
//        pattern.put("/api/logout", "anon");
//        pattern.put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(pattern);
        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager(ShiroRealmService shiroRealmService) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealmService);
        securityManager.setCacheManager(new MemoryConstrainedCacheManager());
        securityManager.setSessionManager(new DefaultWebSessionManager());
        return securityManager;
    }

    @Bean
    public ShiroRealmService myShiroRealm(VerificationCodeCheckService verificationCodeCheckService) {
        return new ShiroRealmService(verificationCodeCheckService);
    }

    @Bean
    public CookieRememberMeManager cookieRememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        //设置RememberMe的cookie有效期为7天
        simpleCookie.setMaxAge(604800);
        cookieRememberMeManager.setCookie(simpleCookie);
        cookieRememberMeManager.setCipherKey(Base64.decode("6ZmI6I2j5Y+R5aSn5ZOlAA=="));
        return cookieRememberMeManager;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserLoginInterceptor(userService));
    }
}
