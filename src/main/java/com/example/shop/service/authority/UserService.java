package com.example.shop.service.authority;

import com.example.shop.generate.User;
import com.example.shop.generate.UserExample;
import com.example.shop.generate.UserMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserMapper userMapper;

    @Inject
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
        } catch (DuplicateKeyException e) {
            getUserByTel(tel).ifPresent(System.out::println);
        }
    }

    public Optional<User> getUserByTel(String tel) {
        UserExample example = new UserExample();
        example.createCriteria().andTelEqualTo(tel);
        List<User> users = userMapper.selectByExample(example);
        User user = users.isEmpty() ? null : users.get(0);
        return Optional.ofNullable(user);
    }
}
