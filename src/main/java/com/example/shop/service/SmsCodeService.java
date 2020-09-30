package com.example.shop.service;

public interface SmsCodeService {
    /**
     * 向指定手机号发送验证码返回正确答案
     * 存在的问题：
     * 1. 防止恶意调用(限流)
     * 2. 防止暴力破解验证码
     * 3. 验证码超时
     *
     * @param tel 用于获取验证码的手机号
     * @return 正确答案
     */
    String senSmsCode(String tel);
}
