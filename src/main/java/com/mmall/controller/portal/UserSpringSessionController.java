package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseStatusEnum;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
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
@RequestMapping("/user/springsession/")
public class UserSpringSessionController {



    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password,
                                      HttpSession session, HttpServletResponse httpResponse) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
            //CookieUtil.writeLoginToken(httpResponse, session.getId());
            //RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        //String loginToken = CookieUtil.readLoginToken(request);
        //CookieUtil.delLoginToken(request, response);
        //RedisShardedPoolUtil.del(loginToken);

        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }


    @RequestMapping(value = "getUserInfo.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session, HttpServletRequest request) {
        //String loginToken = CookieUtil.readLoginToken(request);
        //if (StringUtils.isEmpty(loginToken)) {
        //    return ServerResponse.createByErrorCodeMessage(ResponseStatusEnum.NEED_LOGIN, "用户未登录");
        //}

        //String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        //User curUser = JsonUtil.string2Obj(userJsonStr, User.class);

        User curUser = (User) session.getAttribute(Const.CURRENT_USER);

        if (curUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseStatusEnum.NEED_LOGIN, "用户未登录");
        }
        return iUserService.getUserInfo(curUser.getId());
    }


}
