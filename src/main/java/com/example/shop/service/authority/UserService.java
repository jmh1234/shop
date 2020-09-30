package com.example.shop.service.authority;

import com.example.shop.dao.UserMapper;
import com.example.shop.entity.User;
import com.example.shop.entity.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    User createUserIfNotExist(String tel) {
        User user = new User();
        user.setTel(tel);
        try {
            userMapper.insert(user);
        } catch (Exception e) {
            UserExample example = new UserExample();
            example.createCriteria().andTelEqualTo(tel);
            return userMapper.selectByExample(example).get(0);
        }
        return null;
    }
}
