package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

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

    @Override
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        User user = userMapper.selectLogin(username, MD5Util.MD5EncodeUtf8(password));
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户名或密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        session.setAttribute(Const.CURRENT_USER, user);
        return ServerResponse.createBySuccess("登陆成功", user);
    }

    @Override
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<String> register(User user) {
        if (userMapper.checkUsername(user.getUsername()) > 0) {
            return ServerResponse.createByErrorMessage("用户名已存在");
        }
        if (userMapper.checkEmail(user.getEmail()) > 0) {
            return ServerResponse.createByErrorMessage("邮箱已存在");
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        if (userMapper.insert(user) == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }

        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNoneBlank(type)) {
            if (Const.USERNAME.equals(type) && userMapper.checkUsername(str) > 0) {
                return ServerResponse.createByErrorMessage("用户名已存在");
            }
            if (Const.EMAIL.equals(type) && userMapper.checkEmail(str) > 0) {
                return ServerResponse.createByErrorMessage("邮箱已存在");
            }
            return ServerResponse.createBySuccessMessage("校验成功");
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
    }
}
