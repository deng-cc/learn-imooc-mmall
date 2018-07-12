package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.Map;


/**
 * User: Dulk
 * Date: 2018/7/12
 * Time: 9:43
 */
public interface IOrderService {

    ServerResponse pay(Long orderNo, Integer userId, String path);

    ServerResponse aliCallback(Map<String, String> params);

    ServerResponse<Boolean> queryOrderPayStatus(Integer userId, Long orderNo);
}
