package com.example.demo.mapper;

import com.example.demo.mapper.bean.TestUser;
import org.springframework.stereotype.Repository;

/**
 * TestUserDAO继承基类
 */
@Repository
public interface TestUserDAO extends MyBatisBaseDao<TestUser, Integer> {

    TestUser selectByAccount(String account);   //根据账号查询

}