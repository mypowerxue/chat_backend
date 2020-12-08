package com.example.demo.utils;

public enum ResultStatus {

    SUCCESS(200, "成功"),
    TOKEN(403, "token过期"),
    ERROR(400, "失败");


    /**
     * 返回码
     */
    private int code;
    /**
     * 返回中文结果描述
     */
    private String message;


    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    ResultStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
