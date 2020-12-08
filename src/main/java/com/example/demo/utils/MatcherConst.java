package com.example.demo.utils;

public class MatcherConst {

    public static final String ACCOUNT_MATCHER = "^\\d{1,30}$"; //账号正则

    public static final String PASSWORD_MATCHER = "^(?=.*\\d)(?=.*[a-zA-Z])[\\da-zA-Z]{8,20}$"; //密码正则

}
