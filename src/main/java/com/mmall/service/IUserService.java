package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.common.UsernameTypeEnum;
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

    ServerResponse<String> checkValid(String str, UsernameTypeEnum type);

    ServerResponse<User> getCurUserInfo(HttpSession session);

    ServerResponse<String> getReminder(String username);

    ServerResponse<String> checkReminderAnswer(String username, String reminder, String answer);

    ServerResponse<String> retrievePassword(String username, String newPassword, String token);

    ServerResponse<String> resetPassword(String oldPassword, String newPassword, HttpSession session);

    ServerResponse<User> updateUserInfo(User user, HttpSession session);

    ServerResponse<User> getUserInfo(HttpSession session);

    ServerResponse checkAdminRole(User user);
}
