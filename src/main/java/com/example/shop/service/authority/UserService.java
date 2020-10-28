package com.example.shop.service.authority;

import com.example.shop.generate.User;
import com.example.shop.generate.UserExample;
import com.example.shop.generate.UserMapper;
import com.example.shop.utils.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private final UserMapper userMapper;
    Logger logger = LoggerUtil.getInstance(UserService.class);

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    void createUserIfNotExist(String tel) {
        User user = new User();
        user.setTel(tel);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        try {
            userMapper.insert(user);
        } catch (Exception e) {
            logger.error(LoggerUtil.formatException(e));
            getUserByTel(tel);
        }
    }

    public Optional<User> getUserByTel(String tel) {
        UserExample example = new UserExample();
        example.createCriteria().andTelEqualTo(tel);
        return Optional.ofNullable(userMapper.selectByExample(example).get(0));
    }
}
