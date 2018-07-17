package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.common.UsernameTypeEnum;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * UserServiceImpl.
 *
 * @author Dulk
 * @version 20180611
 * @date 2018/6/11
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;


    private ServerResponse<String> isValid(String str, UsernameTypeEnum type) {
        switch (type) {
            case USERNAME:
                if (userMapper.checkUsername(str) > 0) {
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
                break;
            case EMAIL:
                if (userMapper.checkEmail(str) > 0) {
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
                break;
        }
        return ServerResponse.createBySuccess("校验成功");
    }


    @Override
    public ServerResponse<User> login(String username, String password) {
        User user = userMapper.selectLogin(username, MD5Util.MD5EncodeUtf8(password));
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户名或密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse<String> isValidUsername = isValid(user.getUsername(), UsernameTypeEnum.USERNAME);
        if (!isValidUsername.isSuccess()) {
            return isValidUsername;
        }
        ServerResponse<String> isValidEmail = isValid(user.getEmail(), UsernameTypeEnum.EMAIL);
        if (!isValidEmail.isSuccess()) {
            return isValidEmail;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        if (userMapper.insert(user) == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }

        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, UsernameTypeEnum type) {
        return isValid(str, type);
    }

    @Override
    public ServerResponse<String> getReminder(String username) {
        if (isValid(username, UsernameTypeEnum.USERNAME).isSuccess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String reminder = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(reminder)) {
            return ServerResponse.createBySuccess(reminder);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题为空");
    }

    @Override
    public ServerResponse<String> checkReminderAnswer(String username, String reminder, String answer) {
        if (userMapper.checkAnswer(username, reminder, answer) > 0) {
            String token = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, token);
            return ServerResponse.createBySuccess(token);
        }

        return ServerResponse.createByErrorMessage("问题的答案错误");
    }

    @Override
    public ServerResponse<String> retrievePassword(String username, String newPassword, String clientToken) {
        if (StringUtils.isBlank(clientToken)) {
            return ServerResponse.createByErrorMessage("参数错误，token不得为空");
        }
        if (isValid(username, UsernameTypeEnum.USERNAME).isSuccess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        if (StringUtils.equals(clientToken, token)) {
            String password = MD5Util.MD5EncodeUtf8(newPassword);
            if (userMapper.updatePasswordByUsername(username, password) > 0) {
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        } else {
            return ServerResponse.createByErrorMessage("token错误");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");

    }

    @Override
    public ServerResponse<String> resetPassword(String oldPassword, String newPassword, User user) {
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        if (userMapper.checkPassword(MD5Util.MD5EncodeUtf8(oldPassword), user.getId()) == 0) {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        if (userMapper.updatePasswordByUsername(user.getUsername(), MD5Util.MD5EncodeUtf8(newPassword)) > 0) {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }

        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    @Override
    public ServerResponse<User> updateUserInfo(User user) {
        if (userMapper.checkEmailByUserId(user.getEmail(), user.getId()) > 0) {
            return ServerResponse.createByErrorMessage("email已存在");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        if (userMapper.updateByPrimaryKeySelective(updateUser) > 0) {
            return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
        }

        return ServerResponse.createByErrorMessage("更新用户信息失败");
    }

    @Override
    public ServerResponse<User> getUserInfo(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    @Override
    public ServerResponse checkAdminRole(User user) {
        if (user != null && user.getRole() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }

        return ServerResponse.createByError();
    }


}
