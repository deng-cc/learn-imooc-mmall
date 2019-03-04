package com.mmall.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ExceptionResolver.
 *
 * @author Dulk
 * @version 20190304
 * @date 2019/3/4
 */
@Slf4j
@Component
public class ExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {

        log.error("{} Exception", httpServletRequest.getRequestURI(), e);

        //当使用的是jackson2.x时使用MappingJackson2JsonView
        ModelAndView mv = new ModelAndView(new MappingJacksonJsonView());
        mv.addObject("status", ResponseStatusEnum.ERROR.getCode());
        mv.addObject("msg", "接口异常,详情请查看服务端日志");
        mv.addObject("data", e.toString());

        return mv;
    }
}
