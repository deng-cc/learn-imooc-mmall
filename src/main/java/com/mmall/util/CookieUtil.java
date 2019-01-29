package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CookieUtil.
 *
 * @author Dulk
 * @version 20190129
 * @date 2019/1/29
 */
@Slf4j
public class CookieUtil {

    private static final String COOKIE_DOMAIN = ".mmall.com";
    private static final String COOKIE_NAME = "mmall_login_token";

    //X: domain=".happymmall.com"
    //a: A.happymmall.com          cookie:domain=A.happymmall.com;path="/"
    //b: B.happymmall.com          cookie:domain=B.happymmall.com;path="/"
    //c: A.happymmall.com/test/cc  cookie:domain=A.happymmall.com;path="/test/cc"
    //d: A.happymmall.com/test/dd  cookie:domain=A.happymmall.com;path="/test/dd"
    //e: A.happymmall.com/test     cookie:domain=A.happymmall.com;path="/test"
    public static void writeLoginToken(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");
        cookie.setHttpOnly(true); //禁止js读取cookie，防止脚本攻击
        //单位：秒（-1表示永久）
        //如果maxage不设置则cookie不会写入硬盘；而是写入内存，仅当前页面有效
        cookie.setMaxAge(60 * 60 * 24 * 365);
        log.info("write cookieNmae:{}, cookieValue:{}", cookie.getName(), cookie.getValue());
        response.addCookie(cookie);
    }

    public static String readLoginToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info("read cookieName:{}, cookieValue:{}", cookie.getName(), cookie.getValue());
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
                    log.info("return cookieName:{}, cookieValue:{}", cookie.getName(), cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void delLoginToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
                    cookie.setDomain(COOKIE_DOMAIN);
                    cookie.setPath("/");
                    cookie.setMaxAge(0); //设置0表示删除此cookie
                    log.info("del cookieNmae:{}, cookieValue:{}", cookie.getName(), cookie.getValue());
                    response.addCookie(cookie);
                    return;
                }
            }
        }
    }


}
