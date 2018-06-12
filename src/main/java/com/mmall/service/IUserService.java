package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

import javax.servlet.http.HttpSession;

/**
 * User: Dulk
 * Date: 2018/6/11
 * Time: 14:49
 */
public interface IUserService {

    ServerResponse<User> login(String username, String password, HttpSession session);

    ServerResponse<String> logout(HttpSession session);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);
}
