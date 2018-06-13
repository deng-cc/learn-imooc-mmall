package com.mmall.controller.portal;

import com.mmall.common.ServerResponse;
import com.mmall.common.UsernameTypeEnum;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        return iUserService.login(username, password, session);
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        return iUserService.logout(session);
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
    public ServerResponse<User> getCurUserInfo(HttpSession session) {
        return iUserService.getCurUserInfo(session);
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
    public ServerResponse<String> resetPassword(String oldPassword, String newPassword, HttpSession session) {
        return iUserService.resetPassword(oldPassword, newPassword, session);
    }

    @RequestMapping(value = "updateUserInfo.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserInfo(User user, HttpSession session) {
        return iUserService.updateUserInfo(user, session);
    }

    @RequestMapping(value = "getUserInfo.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session) {
        return iUserService.getUserInfo(session);
    }


}
