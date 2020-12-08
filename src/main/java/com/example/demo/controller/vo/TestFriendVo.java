package com.example.demo.controller.vo;

public class TestFriendVo {

    private Integer userId; //用户Id

    private Integer friendId;   //好友Id

    private String account;

    private String nickName;

    private String picUrl;

    private Integer status; //好友用户状态

    public void setFriendId(Integer friendId) {
        this.friendId = friendId;
    }

    public Integer getFriendId() {
        return friendId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
