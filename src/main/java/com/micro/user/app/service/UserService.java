package com.micro.user.app.service;

import com.micro.user.app.model.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    List<User> getAllUser();
    User getUser(String userId);
}
