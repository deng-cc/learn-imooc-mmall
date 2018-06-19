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

    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, UsernameTypeEnum type);

    ServerResponse<String> getReminder(String username);

    ServerResponse<String> checkReminderAnswer(String username, String reminder, String answer);

    ServerResponse<String> retrievePassword(String username, String newPassword, String token);

    ServerResponse<String> resetPassword(String oldPassword, String newPassword, User user);

    ServerResponse<User> updateUserInfo(User user);

    ServerResponse<User> getUserInfo(Integer userId);

    ServerResponse checkAdminRole(User user);
}
