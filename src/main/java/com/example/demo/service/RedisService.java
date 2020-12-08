package com.example.demo.service;

public interface RedisService {

    void generateToken(String userId);   //创建Token

    boolean checkToken(String userId, String token);   //验证Token是否有效

    String getToken(String userId);    //获取Token

    void renewalToken(String userId);    //续签Token

}
