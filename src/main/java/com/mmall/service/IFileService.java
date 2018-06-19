package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * User: Dulk
 * Date: 2018/6/15
 * Time: 16:15
 */
public interface IFileService {

    String upload(MultipartFile file, String path);

}
