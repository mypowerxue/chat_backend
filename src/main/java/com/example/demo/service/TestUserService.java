package com.example.demo.service;

import com.example.demo.mapper.bean.TestUser;

public interface TestUserService {

    boolean insert(TestUser testUser); //插入

    boolean update(TestUser testUser); //更新

    TestUser selectById(Integer userId); //查询ID

    TestUser selectByAccount(String account); //查询Account

}
