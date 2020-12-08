package com.example.demo.controller.vo;


public class LoginInfoVo {

    private Integer userId;  //好友Id

    private String token;   //用户Token

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
