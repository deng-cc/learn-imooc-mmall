package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseStatusEnum;
import com.mmall.common.ServerResponse;
import com.mmall.common.UsernameTypeEnum;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * UserController.
 *
 * @author Dulk
 * @version 20180611
 * @date 2018/6/11
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password,
                                      HttpSession session, HttpServletResponse httpResponse) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            //session.setAttribute(Const.CURRENT_USER, response.getData());
            CookieUtil.writeLoginToken(httpResponse, session.getId());
            RedisPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String loginToken = CookieUtil.readLoginToken(request);
        CookieUtil.delLoginToken(request, response);
        RedisPoolUtil.del(loginToken);

        //session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    @RequestMapping(value = "checkValid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        UsernameTypeEnum typeEnum = null;
        try {
            typeEnum = UsernameTypeEnum.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ServerResponse.createByErrorMessage("参数转换错误");
        }
        return iUserService.checkValid(str, typeEnum);
    }

    @RequestMapping(value = "getCurUserInfo.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getCurUserInfo(HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
    }

    @RequestMapping(value = "getReminder.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getReminder(String username) {
        return iUserService.getReminder(username);
    }

    @RequestMapping(value = "checkReminderAnswer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkReminderAnswer(String username, String reminder, String answer) {
        return iUserService.checkReminderAnswer(username, reminder, answer);
    }

    @RequestMapping(value = "retrievePassword.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> retrievePassword(String username, String newPassword, String token) {
        return iUserService.retrievePassword(username, newPassword, token);
    }

    @RequestMapping(value = "resetPassword.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(String oldPassword, String newPassword, HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(oldPassword, newPassword, user);
    }

    @RequestMapping(value = "updateUserInfo.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserInfo(User user, HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User curUser = JsonUtil.string2Obj(userJsonStr, User.class);
        if (curUser == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(curUser.getId());
        user.setUsername(curUser.getUsername());
        ServerResponse<User> response = iUserService.updateUserInfo(user);
        if (response.isSuccess()) {
            response.getData().setUsername(curUser.getUsername());
            RedisPoolUtil.setEx(loginToken, JsonUtil.obj2String(response.getData()), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }

        return response;
    }

    @RequestMapping(value = "getUserInfo.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseStatusEnum.NEED_LOGIN, "用户未登录");
        }

        String userJsonStr = RedisPoolUtil.get(loginToken);
        User curUser = JsonUtil.string2Obj(userJsonStr, User.class);

        if (curUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseStatusEnum.NEED_LOGIN, "用户未登录");
        }
        return iUserService.getUserInfo(curUser.getId());
    }


}
