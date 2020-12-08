package com.example.demo.controller.vo;

public class TestMessageVo{

    /**
     * 发送方账号
     */
    private Integer sendId;

    /**
     * 接收方账号
     */
    private Integer receiveId;

    /**
     * 1单聊    2群聊      3通知
     */
    private Integer sendType;

    /**
     * 1文字     2图片     3语音     4语音通话    5视频聊天  6文件传输   7超链接    8红包
     */
    private Integer messageType;

    /**
     * 消息
     */
    private String message;

    /**
     * 消息时间
     */
    private long time;


    public Integer getSendId() {
        return sendId;
    }

    public void setSendId(Integer sendId) {
        this.sendId = sendId;
    }

    public Integer getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(Integer receiveId) {
        this.receiveId = receiveId;
    }

    public Integer getSendType() {
        return sendType;
    }

    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }
}